package com.c1.coins.report.excel.buyreport;

import java.util.Collections;
import java.util.List;

import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.model.User;
import com.c1.coins.report.excel.ExcelRow;
import com.google.common.collect.Lists;

public class RequesterDetailLineExporter {
	public static final List<String> columns = Collections.unmodifiableList(Lists.newArrayList("Beneficiario", "Orden",
			"Oficina", "Cantidad", "Email facturacion", "Estado de Orden en Woo"));
	
	private ExcelRow row;

	protected int identation;


	public RequesterDetailLineExporter(int identation) {
		this.identation = identation;
	}

	public List<String> getHeaderColumns() {
		return columns;
	}


	public RequesterDetailLineExporter use(ExcelRow row) {
		this.row = row;
		return this;
	}

	public ExcelRow export(BuyReportDetailLine line) {
		this.export(line, true);
		return row;
	}

	public ExcelRow export(BuyReportDetailLine line, boolean applyIdentation) {
		if (applyIdentation) {
			row.createEmptyCells(identation);
		}
		User user = line.getUser();
		row.createCell().setCellValue(user.getDisplayName());
		row.createCell().setCellValue(line.getOrderUrl());
		row.createCell().setCellValue(user.getBillingCity());
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(user.getBillingEmail());
		row.createCell().setCellValue(line.getOrder().getStatus());
		return row;
	}

	public void createExcelHeader() {
		row.createEmptyCells(identation);
		row.createCell().setCellValue(columns.get(0));
		row.createCell().setCellValue(columns.get(1));
		row.createCell().setCellValue(columns.get(2));
		row.createCell().setCellValue(columns.get(3));
		row.createCell().setCellValue(columns.get(4));
		row.createCell().setCellValue(columns.get(5));
	}

}
