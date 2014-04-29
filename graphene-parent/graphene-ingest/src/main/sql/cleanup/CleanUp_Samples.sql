--replace characters
select REPLACE (' Hello , How Are You ?', ' ', '' )

--find most common substring (could be used to find least common substring)
declare @T table
(
  ID int identity,
  Data varchar(50)
)

insert into @T values
('KDHFOUDHGOENWFIJ 1114H4363SDFHDHGFDG'),
('GSDLGJSLJSKJDFSG 1114H20SDGDSSFHGSLD'),
('SLSJDHLJKSSDJFKD 1114HJSDHFJKSDKFSGG')

select top 1 substring(T.Data, N.Number, 5) as Word
from @T as T
  cross apply (select N.Number
               from master..spt_values as N
               where N.type = 'P' and
                     N.number between 1 and len(T.Data)-4) as N
group by substring(T.Data, N.Number, 5)                      
order by count(distinct id) desc



--Find all strings that share at least X characters, order by likeness

select x.name, x2.name, MAX(c.seqnum) as OverlapLen
from x cross join
     x x2 cross join
     (select ROW_NUMBER() over (order by (select NULL)) seqnum
      from INFORMATION_SCHEMA.COLUMNS c
     ) c
where LEFT(x.name, c.seqnum) = LEFT(x2.name, c.seqnum) and
      len(x.name) >= c.seqnum and len(x2.name) >= c.seqnum
group by x.name, x.name
order by x.name, OverlapLen desc



-- Perform the cleanup with recursive CTE.
 
WITH Clean (key_col, text_col, ch) 
AS
(SELECT key_col,
        REPLACE(text_col, CHAR(255), ' '),
        255
 FROM Foobar
 UNION ALL
 SELECT key_col,
        CASE WHEN
             CHAR(ch - 1) NOT LIKE '[A-Z]'
             THEN REPLACE(text_col, CHAR(ch - 1), ' ')
             ELSE text_col END,
        ch - 1
 FROM Clean
 WHERE ch > 1)
SELECT key_col, text_col 
FROM Clean
WHERE ch = 1
OPTION (MAXRECURSION 255);




--Here is a different method using utility table with numbers and FOR XML PATH, which is more effective:

WITH Clean (key_col, text_col)
AS
(SELECT key_col, REPLACE(CAST(
        (SELECT CASE 
                  WHEN SUBSTRING(text_col, n, 1) LIKE '[A-Z]' 
                  THEN SUBSTRING(text_col, n, 1) 
                  ELSE '.' 
                END
         FROM (SELECT number
               FROM master..spt_values
               WHERE type = 'P'
                 AND number BETWEEN 1 AND 100) AS Nums(n)
         WHERE n <= LEN(text_col)
         FOR XML PATH('')) AS NVARCHAR(100)), '.', ' ')
 FROM Foobar)
SELECT key_col, text_col
FROM Clean;