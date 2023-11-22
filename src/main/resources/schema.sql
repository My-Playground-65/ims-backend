CREATE TABLE IF NOT EXISTS teachers (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    contact VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS course (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    name VARCHAR(50) NOT NULL ,
    duration_in_months INT NOT NULL
)
CREATE TABLE IF NOT EXISTS teacher_course(
    teacher_id INT,
    course_id INT,
    CONSTRAINT PRIMARY KEY (teacher_id,course_id),
    CONSTRAINT fk_1 FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    CONSTRAINT fk_2 FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);