IF DB_ID('$(DB_NAME)') IS NULL
BEGIN
    DECLARE @sql NVARCHAR(MAX) = N'CREATE DATABASE [' + REPLACE('$(DB_NAME)', ']', ']]') + N'];';
    EXEC sp_executesql @sql;
END
GO
