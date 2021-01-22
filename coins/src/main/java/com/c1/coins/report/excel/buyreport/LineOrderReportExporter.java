package com.c1.coins.report.excel.buyreport;

import java.util.List;

import com.c1.coins.model.LineOrder;
import com.c1.coins.report.excel.ExcelRow;
import com.c1.coins.report.excel.ExcelSheet;
import com.c1.coins.utils.Utils;
import com.google.common.collect.Lists;

public class LineOrderReportExporter {
	private List<String> columnNames = Lists.newArrayList("Orden", "lineaId", "Fecha", "User", "Producto",
			"Coins por unidad", "Cantidad", "Total", "Coins en Catalogo", "USD en Catalogo", "Tipo Moneda", "Errores");

	public void createExcelHeader(ExcelRow header) {
		for (int i = 0; i < columnNames.size(); i++) {
			header.createCell().setCellValue(columnNames.get(i));
		}
	}

	public void export(List<LineOrder> lines, ExcelSheet sheet) {
		ExcelRow header = sheet.createRow();
		createExcelHeader(header);
		for (LineOrder line : lines) {
			ExcelRow row = sheet.createRow();
			export(line, row);
		}

	}

	private void export(LineOrder line, ExcelRow row) {
		row.createCell().setCellValue(line.getParentOrder().getId());
		row.createCell().setCellValue(line.getId());
		row.createCell().setCellValue(line.getParentOrder().getTimeStamp().format(Utils.DATE_FORMATTER));
		row.createCell().setCellValue(line.getParentOrder().getUserName());
		row.createCell().setCellValue(line.getProductName());
		row.createCell().setCellValue(line.getProductCoins());
		row.createCell().setCellValue(line.getQuantity());
		row.createCell().setCellValue(line.getTotalCoins());
		row.createCell().setCellValue(line.getProductCoinsInCatalog());
		row.createCell().setCellValue(line.getProductCurrencyInCatalog().toString());
		row.createCell().setCellValue(String.join("\n", line.getErrors()));
	}

}
