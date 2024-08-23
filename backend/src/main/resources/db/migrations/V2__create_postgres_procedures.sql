CREATE OR REPLACE PROCEDURE create_enum_type(IN type_name TEXT, IN enum_values TEXT[])
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Create the ENUM type if it doesn't exist
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

    -- Create a cast from VARCHAR to the ENUM type if it doesn't exist
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

    -- Create a cast from the ENUM type to VARCHAR if it doesn't exist
    BEGIN
        EXECUTE format(
                'CREATE CAST (%I AS VARCHAR) WITH INOUT AS IMPLICIT',
                type_name
                );
    EXCEPTION
        WHEN duplicate_object THEN
            -- Ignore the exception if the cast already exists
            NULL;
    END;

    -- Create the function to compare ENUM type with TEXT
    BEGIN
        EXECUTE format(
                'CREATE OR REPLACE FUNCTION %I_equals_text(enum_value %I, text_value TEXT) ' ||
                'RETURNS BOOLEAN AS '' ' ||
                'BEGIN ' ||
                'RETURN enum_value::TEXT = text_value; ' ||
                'END; '' LANGUAGE plpgsql;',
                type_name,
                type_name
                );
    EXCEPTION
        WHEN duplicate_function THEN
            -- Ignore the exception if the function already exists
            NULL;
    END;

    -- Create the operator to use the function for comparison
    BEGIN
        EXECUTE format(
                'CREATE OPERATOR = (' ||
                'LEFTARG = %I, ' ||
                'RIGHTARG = TEXT, ' ||
                'PROCEDURE = %I_equals_text, ' ||
                'COMMUTATOR = =, ' ||
                'RESTRICT = eqsel, ' ||
                'JOIN = eqjoinsel);',
                type_name,
                type_name
                );
    EXCEPTION
        WHEN duplicate_function THEN
            -- Ignore the exception if the function already exists
            NULL;
    END;

    -- Create the function to compare TEXT with ENUM type
    BEGIN
        EXECUTE format(
                'CREATE OR REPLACE FUNCTION text_equals_%I(text_value TEXT, enum_value %I) ' ||
                'RETURNS BOOLEAN AS '' ' ||
                'BEGIN ' ||
                'RETURN text_value = enum_value::TEXT; ' ||
                'END; '' LANGUAGE plpgsql;',
                type_name,
                type_name
                );
    EXCEPTION
        WHEN duplicate_function THEN
            -- Ignore the exception if the function already exists
            NULL;
    END;

    -- Create the operator to use the function for comparison
    BEGIN
        EXECUTE format(
                'CREATE OPERATOR = (' ||
                'LEFTARG = TEXT, ' ||
                'RIGHTARG = %I, ' ||
                'PROCEDURE = text_equals_%I, ' ||
                'COMMUTATOR = =, ' ||
                'RESTRICT = eqsel, ' ||
                'JOIN = eqjoinsel);',
                type_name,
                type_name
                );
    EXCEPTION
        WHEN duplicate_function THEN
            -- Ignore the exception if the function already exists
            NULL;
    END;

END;
$$;