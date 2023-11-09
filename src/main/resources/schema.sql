CREATE TABLE PRODUCTION_ORDER (
                                  ID BIGINT IDENTITY PRIMARY KEY,
                                  NAME VARCHAR(50) NOT NULL,
                                  STATE VARCHAR(20) NOT NULL,
                                  EXPECTED_COMPLETION_DATE DATE
);