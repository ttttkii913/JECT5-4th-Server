INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (1, '적극적이에요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (2, '응답이 빨라요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (3, '문서 정리를 잘해요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (4, '팀 분위기를 좋게 만들어요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (5, '작업 속도가 빨라요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (6, '팀원들을 잘 도와줘요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (7, '일정 관리를 잘 해요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (8, '꼼꼼해요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (9, '아이디어가 많아요');
INSERT IGNORE INTO tag(tag_id, tag_name) VALUES (10, '피드백을 잘 반영해요');

INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('1번 항목', '서포트형', '리드형');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('2번 항목', '빠른 작업 속도 중시', '천천히 신중한 고민 중시');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('3번 항목', '상황별 유연한 대처', '철저한 계획 기반 실행');
INSERT IGNORE INTO spectrum_axis(axis_name, left_label, right_label) VALUES ('4번 항목', '냉철한 결과 지향', '따뜻한 관계 지향');

INSERT IGNORE INTO `user`(user_id, email, user_info_delete, username, user_type, marketing_agree) VALUES (1, 'test@sossbar.com', 0, '테스트 계정', 'LOCAL', 0);