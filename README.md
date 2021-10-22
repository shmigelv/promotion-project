# promotion-project

## How to start project
In order to start this project you need to pass aws keys(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY) as environment variables.
It can be done in idea run configuration

## Registration/Login
In order to register new user one should call /api/security/register with user credentials. 
And after that using predefined admin account(username: admin, password: admin) assign a role for created user,
it can be done using endpoint /api/users/{created_user_id}/role 
and pass on of available roles (ROLE_STUDENT, ROLE_ADMIN, ROLE_INSTRUCTOR) as request body.

Once new user is created or, you are using existing credentials you should call /api/security/login with that credentials,
in response there will be jwt token with expiration date that you should use later for header Authorization: Bearer <JWT_TOKEN>