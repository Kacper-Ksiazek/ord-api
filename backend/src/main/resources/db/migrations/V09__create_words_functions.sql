CREATE OR REPLACE FUNCTION count_words_by_field(
    p_field_name TEXT,
    p_language TEXT,
    p_user_id UUID
)
    RETURNS TABLE
            (
                today int,
                week  int,
                month int
            )
AS
$$
DECLARE
    sql TEXT;
BEGIN
    sql := format($f$
        SELECT
            CAST(SUM(CASE WHEN date_trunc('day', %I) = date_trunc('day', current_date) THEN 1 ELSE 0 END) AS integer) AS today,
            CAST(SUM(CASE WHEN date_trunc('week', %I) = date_trunc('week', current_date) THEN 1 ELSE 0 END) AS integer) AS week,
            CAST(SUM(CASE WHEN date_trunc('month', %I) = date_trunc('month', current_date) THEN 1 ELSE 0 END) AS integer) AS month
        FROM words
        WHERE translated_from = $1 AND user_id = $2
    $f$, p_field_name, p_field_name, p_field_name);

    RETURN QUERY EXECUTE sql USING p_language, p_user_id;
END;
$$ LANGUAGE plpgsql;