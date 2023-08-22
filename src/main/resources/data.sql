--이 파일은 스프링 부트 애플리케이션이 실행될 때마다 데이터를 추가하는 쿼리문입니다.
--설정은 application.yml 에서,,
--INSERT INTO member (id, name) VALUES(1, 'name 1');
--INSERT INTO member (id, name) VALUES(2, 'name 2');
--INSERT INTO member (id, name) VALUES(3, 'name 3');

INSERT INTO article (title, content, author, created_at, updated_at) VALUES ('제목 1', '내용 1', 'user1', NOW(), NOW());
INSERT INTO article (title, content, author, created_at, updated_at) VALUES ('제목 2', '내용 2', 'user2', NOW(), NOW());
INSERT INTO article (title, content, author, created_at, updated_at) VALUES ('제목 3', '내용 3', 'user3', NOW(), NOW());