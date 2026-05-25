INSERT IGNORE INTO tag(tag_name) VALUES ('약속을 잘 지켜요');
INSERT IGNORE INTO tag(tag_name) VALUES ('적극적이에요');
INSERT IGNORE INTO tag(tag_name) VALUES ('꼼꼼해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('응답이 빨라요');
INSERT IGNORE INTO tag(tag_name) VALUES ('피드백을 잘 수용해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('문서 정리를 잘해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('아이디어가 많아요');
INSERT IGNORE INTO tag(tag_name) VALUES ('마감을 잘 지켜요');
INSERT IGNORE INTO tag(tag_name) VALUES ('팀 분위기를 좋게 만들어요');
INSERT IGNORE INTO tag(tag_name) VALUES ('시간 조율을 잘해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('실행력이 좋아요');
INSERT IGNORE INTO tag(tag_name) VALUES ('작업 속도가 빨라요');
INSERT IGNORE INTO tag(tag_name) VALUES ('팀원들을 잘 도와줘요');
INSERT IGNORE INTO tag(tag_name) VALUES ('센스가 좋아요');
INSERT IGNORE INTO tag(tag_name) VALUES ('문제 해결을 잘 해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('일정 관리를 잘 해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('끈기있어요');
INSERT IGNORE INTO tag(tag_name) VALUES ('고마움을 잘 표현해요');
INSERT IGNORE INTO tag(tag_name) VALUES ('먼저 친근하게 다가와요');
INSERT IGNORE INTO tag(tag_name) VALUES ('모르는 것을 숨기지 않고 물어봐요');

INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('1번 항목', '서포트형', '리드형');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('2번 항목', '빠른 작업 속도 중시', '천천히 신중한 고민 중시');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('3번 항목', '상황별 유연한 대처', '철저한 계획 기반 실행');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('4번 항목', '냉철한 결과 지향', '따뜻한 관계 지향');

INSERT IGNORE INTO `user`(user_id, email, username, user_type) VALUES (1, 'test@sossbar.com', '테스트 계정', 'LOCAL');