package com.c1.coins.report.excel.buyreport;

import java.util.Iterator;
import java.util.List;

import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.model.BuyReportLine;
import com.c1.coins.report.excel.ExcelRow;
import com.c1.coins.report.excel.ExcelSheet;
import com.c1.coins.utils.Utils;
import com.google.common.collect.Lists;

public class ProductReportExporter {
	private List<String> columnNames = Lists.newArrayList("Cantidad", "Producto", "Precio Convenido",
			"Total Dolar Oficial", "Total Dolar MEP", "Total ARS");
	private RequesterDetailLineExporter detailExporter = new RequesterDetailLineExporter();

	public void createExcelHeader(ExcelRow header) {
		for (int i = 0; i < columnNames.size(); i++) {
			header.createCell().setCellValue(columnNames.get(i));
		}
		this.detailExporter.createExcelHeader(header);
	}

	public void export(List<BuyReportLine> lines, ExcelSheet sheet) {
		ExcelRow header = sheet.createRow();
		createExcelHeader(header);
		for (BuyReportLine line : lines) {
			export(line, sheet.createRow());
		}
		sheet.removeBlankRows();

	}

	private void export(BuyReportLine line, ExcelRow row) {
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(line.getProduct());
		row.createCell().setCellValue(line.getPrice());

		Double totalOficial = 0.0;
		Double totalMep = 0.0;
		Double totalARS = 0.0;
		List<String> errors = Lists.newArrayList();
		switch (line.getCurrency()) {
		case USD_OFICIAL:
			totalOficial = line.getTotal();
			break;
		case USD:
			totalOficial = line.getTotal();
			break;
		case USD_MEP:
			totalMep = line.getTotal();
			break;
		case ARS:
			totalARS = line.getTotal();
			break;
		case USD_REAL:
			errors.add("Producto en USD reales");
		case UNKNOWN:
			errors.add("Producto con moneda desconocida");
		}
		row.createCell().setCellValue(totalOficial);
		row.createCell().setCellValue(totalMep);
		row.createCell().setCellValue(totalARS);
		Iterator<BuyReportDetailLine> i = line.getRequesters().iterator();
		BuyReportDetailLine detail = i.next();
		detail.addErrors(errors);
		detailExporter.use(row).export(detail);
		while (i.hasNext()) {
			detail = i.next();
			detail.addErrors(errors);
			row = row.getParentSheet().createRow();
			row.createEmptyCells(columnNames.size());
			detailExporter.use(row).export(detail);
		}
	}

}
