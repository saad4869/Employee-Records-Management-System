-- Create sequences
CREATE SEQUENCE EMPLOYEE_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;

-- Create USERS table
CREATE TABLE USERS (
                       ID NUMBER(19) PRIMARY KEY,
                       USERNAME VARCHAR2(50) NOT NULL UNIQUE,
                       PASSWORD VARCHAR2(100) NOT NULL,
                       ACTIVE NUMBER(1) DEFAULT 1 NOT NULL,
                       CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       UPDATED_AT TIMESTAMP
);

-- Create USER_ROLES table
CREATE TABLE USER_ROLES (
                            USER_ID NUMBER(19) NOT NULL,
                            ROLE VARCHAR2(20) NOT NULL,
                            CONSTRAINT FK_USER_ROLES_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

-- Create EMPLOYEE table
CREATE TABLE EMPLOYEE (
                           ID NUMBER(19) PRIMARY KEY,
                           EMPLOYEE_ID VARCHAR2(50) NOT NULL UNIQUE,
                           FULL_NAME VARCHAR2(100) NOT NULL,
                           JOB_TITLE VARCHAR2(100) NOT NULL,
                           DEPARTMENT VARCHAR2(50) NOT NULL,
                           HIRE_DATE DATE NOT NULL,
                           STATUS VARCHAR2(20) NOT NULL,
                           EMAIL VARCHAR2(100),
                           PHONE VARCHAR2(20),
                           ADDRESS VARCHAR2(200),
                           USER_ID NUMBER(19),
                           CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                           CREATED_BY VARCHAR2(50),
                           UPDATED_AT TIMESTAMP,
                           UPDATED_BY VARCHAR2(50),
                           CONSTRAINT FK_EMPLOYEE_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

-- Create indexes
CREATE INDEX IDX_USER_USERNAME ON USERS (USERNAME);
CREATE INDEX IDX_EMPLOYEE_EMPID ON EMPLOYEE (EMPLOYEE_ID);
CREATE INDEX IDX_EMPLOYEE_DEPT ON EMPLOYEE (DEPARTMENT);