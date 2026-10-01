# Match auth mode to endpoint security

Anonymous endpoints (`/auth/otp-request`, `/auth/otp-verify`, public QAW) use `auth: none`. All cookie-protected endpoints use `auth: inherit` so Bruno sends the `AUTH-TOKEN` HttpOnly cookie set by OTP verify. Document the login prerequisite in `docs`.

## Good

```bru
docs {
  Run **OTP Request** → **OTP Verify** first.
}

get {
  url: {{baseUrl}}{{apiPrefix}}/users/me
  auth: inherit
}
```

## Bad

```bru
get {
  url: {{baseUrl}}{{apiPrefix}}/users/me
  auth: bearer              # API uses cookie auth, not Bearer
}
```
