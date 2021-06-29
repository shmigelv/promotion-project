CREATE TABLE "admins"
(
    "id" SERIAL PRIMARY KEY
);

CREATE TABLE "instructors"
(
    "id" SERIAL PRIMARY KEY
);

CREATE TABLE "lessons"
(
    "id" SERIAL PRIMARY KEY
);

CREATE TABLE "students"
(
    "id" SERIAL PRIMARY KEY
);

CREATE TABLE "courses"
(
    "id"          SERIAL PRIMARY KEY,
    "lessons"     INTEGER NOT NULL,
    "instructors" INTEGER NOT NULL
);

CREATE TABLE "students_courses"
(
    "courses"  INTEGER NOT NULL,
    "students" INTEGER NOT NULL,
    PRIMARY KEY ("courses", "students")
);

CREATE TABLE "students_marks"
(
    "lessons"  INTEGER NOT NULL,
    "students" INTEGER NOT NULL,
    "mark" INTEGER NOT NULL,
    PRIMARY KEY ("lessons", "students")
);

ALTER TABLE "courses"
    ADD CONSTRAINT "fk_courses__instructors" FOREIGN KEY ("instructors") REFERENCES "instructors" ("id");

ALTER TABLE "courses"
    ADD CONSTRAINT "fk_courses__lessons" FOREIGN KEY ("lessons") REFERENCES "lessons" ("id");

ALTER TABLE "students_courses"
    ADD CONSTRAINT "fk_courses_students__courses" FOREIGN KEY ("courses") REFERENCES "courses" ("id");

ALTER TABLE "students_courses"
    ADD CONSTRAINT "fk_courses_students__students" FOREIGN KEY ("students") REFERENCES "students" ("id");

ALTER TABLE "students_marks"
    ADD CONSTRAINT "fk_lessons_students__lessons" FOREIGN KEY ("lessons") REFERENCES "lessons" ("id");

ALTER TABLE "students_marks"
    ADD CONSTRAINT "fk_lessons_students__students" FOREIGN KEY ("students") REFERENCES "students" ("id")