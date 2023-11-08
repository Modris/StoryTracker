INSERT INTO categories VALUES
(NULL,"Movies"),
(NULL,"Books"),
(NULL,"TV-Shows"),
(NULL,"Comic books");

INSERT INTO status VALUES(NULL,"Ongoing"),(NULL,"Hiatus"),
(NULL,"Completed"),(NULL,"Dropped");

INSERT INTO users VALUES(NULL,"John","123");
INSERT INTO users VALUES(NULL,"Jake","123");

INSERT INTO tracker VALUES(NULL, "Pulp Fiction", 1,3,"Finished",'2022-02-05 10:12:11','2022-02-05 10:12:11', 1);
INSERT INTO tracker VALUES(NULL, "One Piece", 4,1,"Chapter 1030",'2022-02-05 10:12:11','2022-02-05 10:12:11', 1);

INSERT INTO authority VALUES(NULL,"ROLE_ADMIN"),
							(NULL,"ROLE_GUEST"),
                            (NULL,"ROLE_MANAGER");

INSERT INTO user_authorities VALUES(NULL,3,1);
INSERT INTO user_authorities VALUES(NULL,3,2);