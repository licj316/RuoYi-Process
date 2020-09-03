DELETE FROM ACT_RU_VARIABLE;
DELETE FROM ACT_RU_IDENTITYLINK;
DELETE FROM ACT_RU_TASK;
DELETE FROM ACT_RU_ACTINST;
DELETE FROM ACT_RU_EXECUTION;

DELETE FROM ACT_HI_VARINST;
DELETE FROM ACT_HI_IDENTITYLINK;
DELETE FROM ACT_HI_COMMENT;
DELETE FROM ACT_HI_DETAIL;
DELETE FROM ACT_HI_TASKINST;
DELETE FROM ACT_HI_ACTINST;
DELETE FROM ACT_HI_PROCINST;

DELETE FROM FLOW_INST_EXTEND;
DELETE FROM FLOW_DATA;
DELETE FROM FLOW_ATTACHMENT_DETAIL;
DELETE FROM FLOW_ATTACHMENT;
DELETE FROM OA_LEAVE;


# UPDATE FLOW_CONFIG_EXTEND SET PROC_DEF_ID_ = 'leave:3:730389732017704960' WHERE ID_ = 1;

SELECT * FROM ACT_GE_BYTEARRAY;
SELECT * FROM ACT_ID_BYTEARRAY;

SELECT * FROM ACT_RE_MODEL;
SELECT * FROM ACT_RE_DEPLOYMENT;
SELECT * FROM ACT_RE_PROCDEF;

SELECT * FROM ACT_RU_VARIABLE;
SELECT * FROM ACT_RU_IDENTITYLINK;
SELECT * FROM ACT_RU_TASK;
SELECT * FROM ACT_RU_ACTINST;
SELECT * FROM ACT_RU_EXECUTION;

SELECT * FROM ACT_HI_PROCINST;
SELECT * FROM ACT_HI_ACTINST;
SELECT * FROM ACT_HI_TASKINST;
SELECT * FROM ACT_HI_VARINST;
SELECT * FROM ACT_HI_DETAIL;
SELECT * FROM ACT_HI_COMMENT;
SELECT * FROM ACT_HI_IDENTITYLINK;

SELECT * FROM FLOW_CONFIG_EXTEND;
SELECT * FROM FLOW_INST_EXTEND;
SELECT * FROM FLOW_DATA;
SELECT * FROM FLOW_ATTACHMENT_CONFIG;
SELECT * FROM FLOW_ATTACHMENT;
SELECT * FROM FLOW_ATTACHMENT_DETAIL;

SELECT * FROM OA_LEAVE;


SELECT * FROM FLOW_INST_EXTEND WHERE PROC_INS_ID_ = '745681215633362944';
SELECT * FROM FLOW_DATA WHERE PROC_INS_ID_ = '745681215633362944';

UPDATE ACT_RE_MODEL SET CATEGORY_ = '1' where ID_ = '737639419250610176';

SELECT * FROM ACT_RE_MODEL;
SELECT * FROM ACT_RE_DEPLOYMENT;
SELECT * FROM ACT_RE_PROCDEF;


SELECT *
FROM ACT_GE_BYTEARRAY WHERE DEPLOYMENT_ID_ = 'a4fecb61-e1f5-11ea-ae1a-5e33f1ce010f';

SELECT *
FROM ACT_GE_BYTEARRAY
WHERE NAME_ NOT IN (
                    'hist.var-sys_multiinstance_assignees',
                    'var-FLOW_SUBMITTER_ROLE_CODES',
                    'var-formData',
                   'hist.var-FLOW_SUBMITTER_ROLE_CODES',
                   'hist.var-formData',
                   'var-sys_multiinstance_assignees'
    );

SELECT * FROM sys_user;
SELECT * FROM sys_role;



SELECT * FROM FLOW_INST_EXTEND;
SELECT * FROM ACT_RE_PROCDEF;
SELECT * FROM ACT_RE_MODEL;
SELECT * FROM ACT_RE_DEPLOYMENT;
SELECT * FROM ACT_RU_TASK;

SELECT FT.NAME             AS categoryName,
       FIE.CURR_TASK_NAME_ AS taskTitle,
       ARP.KEY_            AS procDefKey,
       ARP.NAME_           AS procDefname,
       FIE.CURR_TASK_NAME_ AS taskName,
       ARP.VERSION_        AS procDefversion,
       ART.CREATE_TIME_    AS createTime
FROM ACT_RU_TASK ART
LEFT JOIN FLOW_INST_EXTEND FIE ON ART.PROC_INST_ID_ = FIE.PROC_INS_ID_
LEFT JOIN ACT_RE_PROCDEF ARP ON ART.PROC_DEF_ID_ = ARP.ID_
LEFT JOIN ACT_RE_DEPLOYMENT ARD ON ARP.DEPLOYMENT_ID_ = ARD.ID_
LEFT JOIN FLOW_TYPE FT ON ARD.CATEGORY_ = FT.ID
WHERE ART.ASSIGNEE_ = 1;


SELECT * FROM ACT_RU_VARIABLE;

        SELECT FT.NAME             AS 'categoryName',
               FIE.CURR_TASK_NAME_ AS 'taskTitle',
               ARP.KEY_            AS 'procDefKey',
               ARP.NAME_           AS 'procDefname',
               FIE.CURR_TASK_NAME_ AS 'taskName',
               ARP.VERSION_        AS 'procDefversion',
               ART.CREATE_TIME_    AS 'createTime',
               ART.ID_             AS 'taskId',
               ART.TASK_DEF_KEY_   AS 'taskDefKey',
               ART.DELEGATION_     AS 'delegateStatus',
               FIE.PROC_DEF_ID_    AS 'procDefId',
               ART.PROC_INST_ID_   AS 'procInsId',
               ART.ASSIGNEE_       AS 'assignee',
               ART.EXECUTION_ID_   AS 'executionId',
               ART.CLAIM_TIME_     AS 'claimTime'
        FROM ACT_RU_TASK ART
        LEFT JOIN FLOW_INST_EXTEND FIE ON ART.PROC_INST_ID_ = FIE.PROC_INS_ID_
        LEFT JOIN ACT_RE_PROCDEF ARP ON ART.PROC_DEF_ID_ = ARP.ID_
        LEFT JOIN ACT_RE_DEPLOYMENT ARD ON ARP.DEPLOYMENT_ID_ = ARD.ID_
        LEFT JOIN FLOW_TYPE FT ON ARD.CATEGORY_ = FT.ID
        WHERE ART.ASSIGNEE_ = 1
        ORDER BY ART.CREATE_TIME_ DESC;

SELECT FT.NAME           AS 'categoryName',
       ARP.NAME_         AS 'procDefname',
       AHT.NAME_         AS 'taskName',
       AHP.END_TIME_     AS 'processFinished',
       ARP.VERSION_      AS 'version',
       AHT.START_TIME_   AS 'createTime',
       AHT.END_TIME_     AS 'endTime',
       AHT.ID_           AS 'taskId',
       AHT.PROC_INST_ID_ AS 'procInsId',
       AHP.PROC_DEF_ID_  AS 'procDefId'
FROM ACT_HI_TASKINST AHT
LEFT JOIN ACT_HI_PROCINST AHP ON AHT.PROC_INST_ID_ = AHP.PROC_INST_ID_
LEFT JOIN ACT_RE_PROCDEF ARP ON AHT.PROC_DEF_ID_ = ARP.ID_
LEFT JOIN ACT_RE_DEPLOYMENT ARD ON ARP.DEPLOYMENT_ID_ = ARD.ID_
LEFT JOIN FLOW_TYPE FT ON ARD.CATEGORY_ = FT.ID
WHERE AHT.ASSIGNEE_ = 1
ORDER BY AHT.START_TIME_ DESC;


SELECT FT.NAME                                                   AS 'categoryName',
       FIE.KEY_ONE_                                              AS 'taskTitle',
       ARP.KEY_                                                  AS 'procDefKey',
       ARP.NAME_                                                 AS 'procDefName',
       ARP.VERSION_                                              AS 'procDefVersion',
       CASE WHEN AHP.END_TIME_ IS NULL THEN '审批中' ELSE '已完成' END AS 'processFinished',
       AHP.START_TIME_                                           AS 'createTime',
       AHP.ID_                                                   AS 'procInsId',
       AHP.PROC_DEF_ID_                                          AS 'procDefId'
FROM ACT_HI_PROCINST AHP
LEFT JOIN ACT_RE_PROCDEF ARP ON AHP.PROC_DEF_ID_ = ARP.ID_
LEFT JOIN ACT_RE_DEPLOYMENT ARD ON ARP.DEPLOYMENT_ID_ = ARD.ID_
LEFT JOIN FLOW_INST_EXTEND FIE ON AHP.PROC_INST_ID_ = FIE.PROC_INS_ID_
LEFT JOIN FLOW_TYPE FT ON ARD.CATEGORY_ = FT.ID
WHERE AHP.START_USER_ID_ = 1
ORDER BY AHP.START_TIME_ DESC


SELECT * FROM SYS_USER;
SELECT * FROM ACT_RE_PROCDEF;
SELECT * FROM ACT_RE_DEPLOYMENT;
SELECT * FROM ACT_GE_BYTEARRAY WHERE DEPLOYMENT_ID_ IS NOT NULL;
SELECT * FROM ACT_HI_PROCINST;

SELECT * FROM ACT_HI_TASKINST WHERE PROC_INST_ID_ = '745681215633362944';
SELECT * FROM ACT_HI_ACTINST WHERE PROC_INST_ID_ = '745681215633362944' ORDER BY START_TIME_ DESC;
SELECT * FROM ACT_RU_ACTINST;
SELECT * FROM ACT_RU_EXECUTION;
SELECT * FROM ACT_RU_TASK;
SELECT * FROM ACT_HI_COMMENT WHERE PROC_INST_ID_ = ''
SELECT * FROM SYS_USER;

SELECT * FROM ACT_RE_DEPLOYMENT;