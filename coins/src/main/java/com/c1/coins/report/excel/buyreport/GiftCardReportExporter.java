package com.c1.coins.report.excel.buyreport;

import java.util.Iterator;
import java.util.List;

import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.model.BuyReportLine;
import com.c1.coins.report.excel.ExcelRow;
import com.c1.coins.report.excel.ExcelSheet;
import com.google.common.collect.Lists;

public class GiftCardReportExporter {
	private List<String> columnNames = Lists.newArrayList("Cantidad", "Producto", "USD", "Total");
	private RequesterDetailLineExporter detailExporter = new RequesterDetailLineExporter();

	public void createExcelHeader(ExcelRow header) {
		for (int i = 0; i < columnNames.size(); i++) {
			header.createCell().setCellValue(columnNames.get(i));
		}
		detailExporter.createExcelHeader(header);
	}

	public void export(List<BuyReportLine> lines, ExcelSheet sheet) {
		ExcelRow header = sheet.createRow();
		createExcelHeader(header);
		for (BuyReportLine line : lines) {
			ExcelRow row = sheet.createRow();
			export(line, row);
		}
		// sheet.removeBlankRows();

	}

	private void export(BuyReportLine line, ExcelRow row) {
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(line.getProduct());
		row.createCell().setCellValue(line.getProductPrice());
		row.createCell().setCellValue(line.getTotalPrice());
		if (line.getRequesters().isEmpty()) {
			return;
		}
		Iterator<BuyReportDetailLine> i = line.getRequesters().iterator();
		BuyReportDetailLine detail = i.next();
		detailExporter.use(row).export(detail);
		while (i.hasNext()) {
			detail = i.next();
			row = row.getParentSheet().createRow();
			row.createEmptyCells(columnNames.size());
			detailExporter.use(row).export(detail);
		}
	}

}
