package com.c1.coins.report.excel;

import org.apache.poi.ss.usermodel.Sheet;

public class ExcelSheet {

	private Sheet excelSheet;
	private int lastRowIndex = -1;

	public ExcelSheet(Sheet parentSheet) {
		super();
		this.excelSheet = parentSheet;
	}

	public ExcelRow createRow() {
		lastRowIndex++;
		return new ExcelRow(excelSheet.createRow(lastRowIndex), this);
	}

}
