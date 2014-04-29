

--find a specific column name
USE AdventureWorks
GO
SELECT t.name AS table_name,
SCHEMA_NAME(schema_id) AS schema_name,
c.name AS column_name
FROM sys.tables AS t
INNER JOIN sys.columns c ON t.OBJECT_ID = c.OBJECT_ID
WHERE c.name LIKE '%EmployeeID%'
ORDER BY schema_name, table_name;


--find all the column names
SELECT t.name AS table_name,
SCHEMA_NAME(schema_id) AS schema_name,
c.name AS column_name
FROM sys.tables AS t
INNER JOIN sys.columns c ON t.OBJECT_ID = c.OBJECT_ID 
ORDER BY schema_name, table_name;


--You can also get other objects (Tables/Views/Functions) when with the following (should work on sql2000 too):

select
o.name as tableName,
c.name as ColumnName,
o.type as ObjectType,
u.name as SchemaName
from syscolumns c
inner join sysobjects o
on o.id=c.id
inner join sysusers u
on u.uid= o.uid
where
c.name like ‘%EmployeeID%’

--DECLARE TABLE NAME VARIABLE DYNAMICALLY
DECLARE @table_name varchar(max)
SET @table_name

