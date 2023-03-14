CREATE FUNCTION update_timestamps_on_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
    BEGIN
        NEW.insert_timestamp = now();
        NEW.update_timestamp = now();
    END;
$$;

CREATE FUNCTION update_timestamps_on_update()
RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
    BEGIN
        NEW.update_timestamp = now();
    END;
$$;

CREATE TRIGGER portfolios_timestamps_on_insert
BEFORE INSERT ON portfolios
FOR EACH ROW
EXECUTE FUNCTION update_timestamps_on_insert();

CREATE TRIGGER shares_owned_timestamps_on_insert
BEFORE INSERT ON shares_owned
FOR EACH ROW
EXECUTE FUNCTION update_timestamps_on_insert();

CREATE TRIGGER portfolios_timestamps_on_update
BEFORE UPDATE ON portfolios
FOR EACH ROW
EXECUTE FUNCTION update_timestamps_on_update();

CREATE TRIGGER shares_owned_timestamps_on_update
BEFORE UPDATE ON shares_owned
FOR EACH ROW
EXECUTE FUNCTION update_timestamps_on_update();