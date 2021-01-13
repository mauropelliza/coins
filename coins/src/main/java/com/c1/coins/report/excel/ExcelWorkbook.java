package com.c1.coins.report.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.Maps;

public class ExcelWorkbook {

	private Workbook excelWorkbook = new HSSFWorkbook();
	private Map<String, ExcelSheet> sheets = Maps.newLinkedHashMap();

	public ExcelSheet addSheet(String name) {
		ExcelSheet sheet = new ExcelSheet(excelWorkbook.createSheet(name));
		this.sheets.put(name, sheet);
		return sheet;
	}

	public ExcelSheet getSheet(String name) {
		return sheets.get(name);
	}

	public byte[] toBytes() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			this.excelWorkbook.write(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stream.toByteArray();
	}

}
