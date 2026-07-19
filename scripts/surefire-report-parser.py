#!/usr/bin/env python3
"""Parse Maven Surefire XML reports for live progress and final summaries."""

from __future__ import annotations

import glob
import json
import os
import sys
import xml.etree.ElementTree as ET


def _first_line(text: str | None, limit: int = 140) -> str:
    if not text:
        return ""
    line = text.strip().splitlines()[0] if text.strip() else ""
    if len(line) > limit:
        return line[: limit - 3] + "..."
    return line


def _readable_class_name(classname: str) -> str:
    return classname.replace("$", ".")


def parse_reports(reports_dir: str) -> dict:
    totals = {
        "tests": 0,
        "passed": 0,
        "failures": 0,
        "errors": 0,
        "skipped": 0,
    }
    failed_cases: list[dict[str, str]] = []

    if not os.path.isdir(reports_dir):
        return {"totals": totals, "failed_cases": failed_cases}

    for path in sorted(glob.glob(os.path.join(reports_dir, "TEST-*.xml"))):
        try:
            tree = ET.parse(path)
        except ET.ParseError:
            # Report file may still be written by Surefire.
            continue

        root = tree.getroot()
        for testcase in root.findall("testcase"):
            totals["tests"] += 1
            name = testcase.get("name", "?")
            classname = testcase.get("classname", root.get("name", "?"))

            failure = testcase.find("failure")
            error = testcase.find("error")
            skipped = testcase.find("skipped")

            if failure is not None:
                totals["failures"] += 1
                failed_cases.append(
                    {
                        "class": _readable_class_name(classname),
                        "name": name,
                        "kind": "FAIL",
                        "message": _first_line(failure.get("message") or failure.text),
                    }
                )
            elif error is not None:
                totals["errors"] += 1
                failed_cases.append(
                    {
                        "class": _readable_class_name(classname),
                        "name": name,
                        "kind": "ERROR",
                        "message": _first_line(error.get("message") or error.text),
                    }
                )
            elif skipped is not None:
                totals["skipped"] += 1
            else:
                totals["passed"] += 1

    return {"totals": totals, "failed_cases": failed_cases}


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: surefire-report-parser.py <surefire-reports-dir>", file=sys.stderr)
        return 2

    print(json.dumps(parse_reports(sys.argv[1])))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
