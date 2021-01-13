package com.c1.coins.report.excel.buyreport;

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

		String totalOficial = "";
		String totalMep = "";
		String totalARS = "";
		switch (line.getCurrency()) {
		case USD_OFICIAL:
			totalOficial = line.getTotal().toString();
			break;
		case USD:
			totalOficial = line.getTotal().toString();
			break;
		case USD_MEP:
			totalMep = line.getTotal().toString();
			break;
		case ARS:
			totalARS = line.getTotal().toString();
			break;
		case USD_REAL:
			throw new RuntimeException("Un producto no puede estar en USD reales: " + line.getProduct());
		case UNKNOWN:
			throw new RuntimeException("Un producto no puede estar con currency UNKNOWN: " + line.getProduct());
		}
		row.createCell().setCellValue(totalOficial);
		row.createCell().setCellValue(totalOficial);
		row.createCell().setCellValue(totalMep);
		row.createCell().setCellValue(totalARS);
		for (BuyReportDetailLine detail : line.getRequesters()) {
			detailExporter.use(row).export(detail);
			row = row.getParentSheet().createRow();
			row.createEmptyCells(columnNames.size());
		}
	}

}
