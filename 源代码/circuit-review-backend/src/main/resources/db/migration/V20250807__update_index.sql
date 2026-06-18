CREATE INDEX IF NOT EXISTS "userrole_user_id_index" ON "urm_userrole" USING btree ( "user_id");

CREATE INDEX IF NOT EXISTS "department_f_id_index" ON "urm_department" USING btree ( "f_id");

CREATE INDEX IF NOT EXISTS "department_name_index" ON "urm_department" USING btree ( "name");

CREATE INDEX IF NOT EXISTS "file_file_id_index" ON "dlsc_file" USING btree ( "file_id");

CREATE INDEX IF NOT EXISTS "file_department_id_index" ON "dlsc_file" USING btree ( "department_id");

CREATE INDEX IF NOT EXISTS "file_owner_id_index" ON "dlsc_file" USING btree ( "owner_id");

CREATE INDEX IF NOT EXISTS "file_is_recycle_index" ON "dlsc_file" USING btree ( "is_recycle");

CREATE INDEX IF NOT EXISTS "file_create_user_index" ON "dlsc_file" USING btree ( "create_user");

CREATE INDEX IF NOT EXISTS "review_result_file_id_index" ON "dlsc_review_result" USING btree ( "file_id");

CREATE INDEX IF NOT EXISTS "review_result_review_time_index" ON "dlsc_review_result" USING btree ( "review_time");

CREATE INDEX IF NOT EXISTS "review_result_status_index" ON "dlsc_review_result" USING btree ( "status");

CREATE INDEX IF NOT EXISTS "review_result_create_date_index" ON "dlsc_review_result" ( "create_date");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_id_index" ON "dlsc_review_result_detail" ( "result_id");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_rule_id_index" ON "dlsc_review_result_detail" ( "rule_id");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_device_type_index" ON "dlsc_review_result_detail" ( "device_type");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_is_passed_index" ON "dlsc_review_result_detail" ( "is_passed");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_create_date_index" ON "dlsc_review_result_detail" ( "create_date");

CREATE INDEX IF NOT EXISTS "rule_type_index" ON "dlsc_rule" USING btree ( "type");

CREATE INDEX IF NOT EXISTS "rule_create_date_index" ON "dlsc_rule" USING btree ( "create_date");
