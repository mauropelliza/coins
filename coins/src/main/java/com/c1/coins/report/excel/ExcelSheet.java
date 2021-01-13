package com.c1.coins.report.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.assertj.core.util.Lists;

import com.google.common.collect.Maps;

public class ExcelSheet {

	private Sheet excelSheet;
	private int lastRowIndex = -1;
	private Map<Integer, ExcelRow> rows = Maps.newLinkedHashMap();

	public ExcelSheet(Sheet parentSheet) {
		super();
		this.excelSheet = parentSheet;
	}

	public ExcelRow createRow() {
		lastRowIndex++;
		ExcelRow row = new ExcelRow(excelSheet.createRow(lastRowIndex), this);
		rows.put(lastRowIndex, row);
		return row;
	}

	public void removeBlankRows() {
		List<ExcelRow> rowsToDelete = Lists.newArrayList();
		for (int i = 0; i <= lastRowIndex; i++) {
			ExcelRow row = rows.get(i);
			if (row.isBlank()) {
				rowsToDelete.add(row);
			}
		}
		rowsToDelete.stream().forEach(row -> {
			Row excelRow = row.getRow();
			rows.remove(excelRow.getRowNum());
			excelSheet.removeRow(excelRow);
		});
		lastRowIndex -= rowsToDelete.size();
	}

}
