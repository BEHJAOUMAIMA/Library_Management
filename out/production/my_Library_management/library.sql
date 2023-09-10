CREATE TRIGGER  before_insert_book
BEFORE INSERT ON books
FOR EACH ROW
BEGIN
    DECLARE book_id INT;
    SELECT book_id INTO book_id FROM books WHERE book_title = NEW.book_title AND author_id = NEW.author_id LIMIT 1;
    IF book_id IS NOT NULL THEN
        SET NEW.book_quantity = NEW.book_quantity + (SELECT book_quantity FROM books WHERE book_id = book_id);

        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Le livre existe déjà dans la base de données. La quantité a été mise à jour.';
END IF;
END;

CREATE TRIGGER decrement_book_quantity
    AFTER UPDATE ON BorrowedCopy
    FOR EACH ROW
BEGIN
    IF NEW.borrowedCopy_status = 'lost' THEN
    UPDATE books SET book_quantity = book_quantity - 1 WHERE book_id = NEW.book_id;
END IF;
END;



CREATE EVENT CheckLoanStatus
ON SCHEDULE EVERY 2 HOUR
DO
BEGIN
UPDATE borrowedCopies bc
    INNER JOIN loans l ON bc.loan_id = l.loan_id
    SET bc.borrowedCopy_status = 'Losted'
WHERE l.return_date < NOW();
END;


