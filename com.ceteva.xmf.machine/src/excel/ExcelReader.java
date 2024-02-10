package excel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

	public static final String SAMPLE_XLSX_FILE_PATH = "/Users/clarkt1/Dropbox/Aston_Files/Staff/Performance/CoreHRA03 - Current Employees & Appointments - v3.xlsx";//"src/excel/data/bikes.xlsx";

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy", new Locale("us"));

	public static Term readWorkbook(String path) {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new File(path));
			Term term = getTerm(workbook);
			workbook.close();
			return term;
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			return new Term("ExcelError", new Atom("String", e.toString()));
		}
	}

	public static void main(String[] args) throws IOException, InvalidFormatException {

		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

		// Retrieving the number of sheets in the Workbook
		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		/*
		 * ============================================================= Iterating over all the sheets in the workbook (Multiple ways) =============================================================
		 */

		// 1. You can obtain a sheetIterator and iterate over it
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		System.out.println("Retrieving Sheets using Iterator");
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			System.out.println("=> " + sheet.getSheetName());
		}

		// 2. Or you can use a for-each loop
		System.out.println("Retrieving Sheets using for-each loop");
		for (Sheet sheet : workbook) {
			System.out.println("=> " + sheet.getSheetName());
		}

		// 3. Or you can use a Java 8 forEach with lambda
		System.out.println("Retrieving Sheets using Java 8 forEach with lambda");
		workbook.forEach(sheet -> {
			System.out.println("=> " + sheet.getSheetName());
		});

		/*
		 * ================================================================== Iterating over all the rows and columns in a Sheet (Multiple ways)
		 * ==================================================================
		 */

		// Getting the Sheet at index zero
		Sheet sheet = workbook.getSheetAt(0);

		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();

		// 3. Or you can use Java 8 forEach loop with lambda
		System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
		sheet.forEach(row -> {
			row.forEach(cell -> {
				printCellValue(cell);
			});
			System.out.println();
		});

		// Closing the workbook
		workbook.close();

		System.out.println(readWorkbook(SAMPLE_XLSX_FILE_PATH));
	}

	private static void printCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			System.out.print(cell.getBooleanCellValue());
			break;
		case STRING:
			System.out.print(cell.getRichStringCellValue().getString());
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				System.out.print(cell.getDateCellValue());
			} else {
				System.out.print(cell.getNumericCellValue());
			}
			break;
		case FORMULA:
			System.out.print(cell.getCellFormula());
			break;
		case BLANK:
			System.out.print("");
			break;
		default:
			System.out.print("");
		}

		System.out.print("\t");
	}

	private static Term getTerm(Workbook book) {
		return new Term("XWorkbook", getSheets(book));
	}

	private static Value[] getSheets(Workbook book) {
		Vector<Value> sheets = new Vector<Value>();
		book.forEach(sheet -> {
			sheets.add(getTerm(sheet));
		});
		return sheets.toArray(new Value[0]);
	}

	private static Term getTerm(Sheet sheet) { 
		return new Term("XSheet", getRows(sheet));
	}

	private static Value[] getRows(Sheet sheet) {
		Vector<Value> rows = new Vector<Value>();
		rows.add(new Atom("String",sheet.getSheetName()));
		sheet.forEach(row -> {
			rows.add(getTerm(row));
		});
		return rows.toArray(new Value[0]);
	}

	private static Term getTerm(Row row) {
		return new Term("XRow", getCells(row));
	}

	private static Value[] getCells(Row row) {
		Vector<Value> cells = new Vector<Value>();
		row.forEach(cell -> {
			cells.add(getCell(cell));
		});
		return cells.toArray(new Value[0]);
	}

	private static Value getCell(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue() ? new Atom("Boolean", true) : new Atom("Boolean", false);
		case STRING:
			return new Atom("String", cell.getRichStringCellValue().getString());
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return new Atom("Date", simpleDateFormat.format(cell.getDateCellValue()));
			} else {
				return new Atom("Float", cell.getNumericCellValue());
			}
		case FORMULA:
			return new Atom("Formula", cell.getCellFormula());
		case BLANK:
			return new Atom("XBlank",null);
		default:
			return new Atom("XUnknown",null);
		}
	}
}