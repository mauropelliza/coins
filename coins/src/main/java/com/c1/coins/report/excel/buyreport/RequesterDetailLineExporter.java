package com.c1.coins.report.excel.buyreport;

import java.util.Collections;
import java.util.List;

import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.model.User;
import com.c1.coins.report.excel.ExcelRow;
import com.google.common.collect.Lists;

public class RequesterDetailLineExporter {
	public static final List<String> columns = Collections.unmodifiableList(Lists.newArrayList("Beneficiario", "Orden",
			"Oficina", "Cantidad", "Email facturacion", "Estado de Orden en Woo", "Errores"));

	private ExcelRow row;

	public RequesterDetailLineExporter() {
	}

	public void createExcelHeader(ExcelRow header) {
		for (int i = 0; i < columns.size(); i++) {
			header.createCell().setCellValue(columns.get(i));
		}
	}

	public RequesterDetailLineExporter use(ExcelRow row) {
		this.row = row;
		return this;
	}

	public ExcelRow export(BuyReportDetailLine line) {
		User user = line.getUser();
		row.createCell().setCellValue(user.getDisplayName());
		row.createCell().setCellValue(line.getOrderUrl());
		row.createCell().setCellValue(user.getBillingCity());
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(user.getBillingEmail());
		row.createCell().setCellValue(line.getOrder().getStatus());
		row.createCell().setCellValue(line.getErrors());
		return row;
	}
}
