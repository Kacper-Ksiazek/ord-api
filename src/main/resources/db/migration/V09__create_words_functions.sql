CREATE OR REPLACE FUNCTION count_words_by_field(
    p_field_name TEXT,
    p_language TEXT,
    p_user_id UUID
)
    RETURNS TABLE
            (
                today int,
                week  int,
                month int
            )
AS
$$
DECLARE
    sql TEXT;
BEGIN
    IF p_field_name = 'first_completed_at' THEN
        sql := $f$
            SELECT
                CAST(SUM(CASE WHEN date_trunc('day', wp.first_completed_at) = date_trunc('day', current_date) THEN 1 ELSE 0 END) AS integer) AS today,
                CAST(SUM(CASE WHEN date_trunc('week', wp.first_completed_at) = date_trunc('week', current_date) THEN 1 ELSE 0 END) AS integer) AS week,
                CAST(SUM(CASE WHEN date_trunc('month', wp.first_completed_at) = date_trunc('month', current_date) THEN 1 ELSE 0 END) AS integer) AS month
            FROM word_progress wp
                INNER JOIN words w ON w.id = wp.word_id
            WHERE w.language = $1 AND wp.user_id = $2
        $f$;
    ELSE
        sql := format($f$
            SELECT
                CAST(SUM(CASE WHEN date_trunc('day', %I) = date_trunc('day', current_date) THEN 1 ELSE 0 END) AS integer) AS today,
                CAST(SUM(CASE WHEN date_trunc('week', %I) = date_trunc('week', current_date) THEN 1 ELSE 0 END) AS integer) AS week,
                CAST(SUM(CASE WHEN date_trunc('month', %I) = date_trunc('month', current_date) THEN 1 ELSE 0 END) AS integer) AS month
            FROM words
            WHERE language = $1 AND user_id = $2
        $f$, p_field_name, p_field_name, p_field_name);
    END IF;

    RETURN QUERY EXECUTE sql USING p_language, p_user_id;
END;
$$ LANGUAGE plpgsql;
