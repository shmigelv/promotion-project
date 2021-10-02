CREATE TABLE "users"
(
    "id"       SERIAL PRIMARY KEY,
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "role"     TEXT
);

CREATE TABLE "courses"
(
    "id"    SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL
);

CREATE TABLE "instructor_courses"
(
    "id"        SERIAL PRIMARY KEY,
    "user_id"   INTEGER NOT NULL,
    "course_id" INTEGER NOT NULL
);

ALTER TABLE "instructor_courses"
    ADD CONSTRAINT "fk_instructor_courses__user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "instructor_courses"
    ADD CONSTRAINT "fk_instructor_courses__course_id" FOREIGN KEY ("course_id") REFERENCES "courses" ("id") ON DELETE CASCADE;

CREATE TABLE "student_courses" (
    "id"        SERIAL PRIMARY KEY,
    "user_id"   INTEGER NOT NULL,
    "course_id" INTEGER NOT NULL
);

ALTER TABLE "student_courses"
    ADD CONSTRAINT "fk_student_courses__user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "student_courses"
    ADD CONSTRAINT "fk_student_courses__course_id" FOREIGN KEY ("course_id") REFERENCES "courses" ("id") ON DELETE CASCADE;

CREATE TABLE "lessons"
(
    "id"        SERIAL PRIMARY KEY,
    "title"     TEXT    NOT NULL,
    "course_id" INTEGER NOT NULL
);

ALTER TABLE "lessons"
    ADD CONSTRAINT "fk_lessons__course_id" FOREIGN KEY ("course_id") REFERENCES "courses" ("id") ON DELETE CASCADE;

CREATE TABLE "homeworks"
(
    "id"                SERIAL PRIMARY KEY,
    "user_id"           INTEGER NOT NULL,
    "lesson_id"         INTEGER NOT NULL,
    "mark"              INTEGER,
    "homework_file_key" TEXT
);

ALTER TABLE "homeworks"
    ADD CONSTRAINT "fk_homeworks__lesson_id" FOREIGN KEY ("lesson_id") REFERENCES "lessons" ("id") ON DELETE CASCADE;

ALTER TABLE "homeworks"
    ADD CONSTRAINT "fk_homeworks__user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

CREATE TABLE "course_feedbacks"
(
    "id"        SERIAL PRIMARY KEY,
    "user_id"   INTEGER NOT NULL,
    "course_id" INTEGER NOT NULL,
    "feedback"  TEXT    NOT NULL
);

ALTER TABLE "course_feedbacks"
    ADD CONSTRAINT "fk_course_feedbacks__course_id" FOREIGN KEY ("course_id") REFERENCES "courses" ("id") ON DELETE CASCADE;

ALTER TABLE "course_feedbacks"
    ADD CONSTRAINT "fk_course_feedbacks__user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;