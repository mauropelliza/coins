package com.c1.coins.report.excel.buyreport;

import java.util.List;

import com.c1.coins.model.BuyReportLine;
import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.report.excel.ExcelRow;
import com.c1.coins.report.excel.ExcelSheet;
import com.c1.coins.utils.CSVLine;
import com.google.common.collect.Lists;

public class GiftCardReportExporter {
	private List<String> columnNames = Lists.newArrayList("Cantidad", "Producto", "USD", "Total");
	private RequesterDetailLineExporter detailExporter = new RequesterDetailLineExporter(columnNames.size());

	public List<String> getHeaderColumns() {
		columnNames.addAll(detailExporter.getHeaderColumns());
		return columnNames;
	}

	public void createExcelHeader(ExcelRow header) {
		for(int i=0;i<columnNames.size();i++) {
			header.createCell().setCellValue(columnNames.get(i));
		}
	}

	public void export(List<BuyReportLine> lines, ExcelSheet sheet) {
		ExcelRow header = sheet.createRow();
		createExcelHeader(header);
		for (BuyReportLine line : lines) {
			ExcelRow row = sheet.createRow();
			export(line, row);
		}

	}

	private void export(BuyReportLine line, ExcelRow row) {
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(line.getProduct());
		row.createCell().setCellValue(line.getPrice());
		row.createCell().setCellValue(line.getTotal());
		for (BuyReportDetailLine detail : line.getRequesters()) {
			detailExporter.use(row).export(detail);
			row = row.getParentSheet().createRow();
			row.createEmptyCells(columnNames.size());
		}
	}

}
