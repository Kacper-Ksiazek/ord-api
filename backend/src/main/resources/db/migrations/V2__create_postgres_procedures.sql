CREATE OR REPLACE PROCEDURE create_enum_type(
    type_name TEXT,
    enum_values TEXT[]
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    BEGIN
        EXECUTE format(
                'CREATE TYPE %I AS ENUM (%s)',
                type_name,
                array_to_string(
                        array(
                                SELECT quote_literal(value)
                                FROM unnest(enum_values) AS value
                        ),
                        ', '
                )
                );
    EXCEPTION
        WHEN duplicate_object THEN
            -- Ignore the exception if the type already exists
            NULL;
    END;

    BEGIN
        EXECUTE format(
                'CREATE CAST (VARCHAR AS %I) WITH INOUT AS IMPLICIT',
                type_name
                );
    EXCEPTION
        WHEN duplicate_object THEN
            -- Ignore the exception if the cast already exists
            NULL;
    END;
END;
$$;
