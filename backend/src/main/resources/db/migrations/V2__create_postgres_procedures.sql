CREATE OR REPLACE PROCEDURE create_enum_type(
    type_name TEXT,
    enum_values TEXT[]
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Step 1: Create the enum type if it doesn't exist
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

    -- Step 2: Create a cast from VARCHAR to the enum type
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

    -- Step 3: Create a cast from TEXT to the enum type
    BEGIN
        EXECUTE format(
                'CREATE CAST (TEXT AS %I) WITH INOUT AS IMPLICIT',
                type_name
                );
    EXCEPTION
        WHEN duplicate_object THEN
            -- Ignore the exception if the cast already exists
            NULL;
    END;
END;
$$;