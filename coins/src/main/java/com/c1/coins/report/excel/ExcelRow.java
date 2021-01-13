package com.c1.coins.report.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.c1.coins.utils.Utils;

public class ExcelRow {

	private ExcelSheet parentSheet;
	private Row excelRow;
	private int lastCellIndex = -1;

	public ExcelRow(Row excelRow, ExcelSheet parentSheet) {
		super();
		this.excelRow = excelRow;
		this.parentSheet = parentSheet;
	}

	public Cell createCell() {
		lastCellIndex++;
		return excelRow.createCell(lastCellIndex);

	}

	public ExcelSheet getParentSheet() {
		return this.parentSheet;
	}

	public void createEmptyCells(int quantity) {
		int cellIndex = 0;
		while (cellIndex < quantity) {
			excelRow.createCell(cellIndex++);
		}
		lastCellIndex += quantity;
	}

	public boolean isBlank() {
		for (int i = 0; i < excelRow.getLastCellNum(); i++) {
			String value = null;
			try {
				value = excelRow.getCell(i).getStringCellValue();
			} catch (IllegalStateException e) {
				value = Double.toString(excelRow.getCell(i).getNumericCellValue());
			}
			if (!Utils.isBlank(value)) {
				return false;
			}
		}
		return true;
	}

	public Row getRow() {
		return excelRow;
	}

}
