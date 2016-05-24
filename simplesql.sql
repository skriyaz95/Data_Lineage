CREATE VIEW HRMGR.V_EMP
AS
  SELECT (EH.FNAME+E.ENAME)
   AS EMPLOYEE_NAME, D.NAME, (E.SAL+D.PERCENTAGE) AS BONUS
  FROM  HRMGR.EMP E, HRMGR.EMP_HIST EH, HRMGR.DEPT D
  WHERE E.DEPTID=E.DEPTID; 