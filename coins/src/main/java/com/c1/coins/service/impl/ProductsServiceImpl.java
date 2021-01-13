package com.c1.coins.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c1.coins.csv.ProductToCsvSerializer;
import com.c1.coins.csv.ResultsToCsvSerializer;
import com.c1.coins.model.Product;
import com.c1.coins.model.ProductDetail;
import com.c1.coins.model.ProductDetailWithAction;
import com.c1.coins.model.ProductPrice;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.ProductUpdaterService;
import com.c1.coins.service.ProductsService;
import com.c1.coins.service.WooCommerceService;
import com.c1.coins.utils.CsvActionsEnum;
import com.c1.coins.utils.Utils;
import com.c1.coins.utils.VisibilityEnum;
import com.c1.coins.validators.CsvReadingException;
import com.c1.coins.validators.ProductValidator;
import com.google.common.collect.Lists;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class ProductsServiceImpl implements ProductsService {
	private static final Integer ROW_LENGTH = 9;
	private static final String LINE_ENDING = StringUtils.CR + StringUtils.LF;

	@Autowired
	private WooCommerceService wooCommerceService;

	@Autowired
	private DBRepository dBRepository;

	@Value("${spring.api.server}")
	private String woocommerceServer;

	private String csvSeparator = ",";

	@Value("${fields-per-row}")
	private int fiedsPerRow;

	@Autowired
	private ProductValidator productValidator;

	@Autowired
	private ProductReportValidator reportValidator;

	@Autowired
	private ProductUpdaterService productUpdaterService;

	@Autowired
	private ResultsToCsvSerializer serializer;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> getAllProductsFromWooc() {

		// Setup client
		WooCommerce wooCommerce = wooCommerceService.getWooCommerce(woocommerceServer);

		// Get all with request parameters
		Map<String, String> params = new HashMap<>();
		params.put("per_page", "100");
		params.put("offset", "0");
		List products = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);

		System.out.println("amount of products: " + products.size());

		return products;
	}

	@Override
	public List<ProductPrice> getProductPrices() {
		Map<String, ProductPrice> productMap = dBRepository.getProductPrices();
		if (productMap.isEmpty())
			return Lists.newArrayList();

		return productMap.values().stream().collect(Collectors.toList());
	}

	@Override
	public void setVisibility(Integer productId, String visibility) {
		productValidator.validateVisibility(visibility);
		productValidator.validateProductExistance(productId);
		if (VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility)) {
			productUpdaterService.showProduct(productId);
		} else {
			productUpdaterService.hideProduct(productId);
		}
	}

	@Override
	public List<ProductDetail> getProductsFromWoo() {
		return dBRepository.getProductsDetail();
	}

	@Override
	public void comparar() {
		List<ProductDetail> dbList = dBRepository.getProductsDetail();
		Map<String, Product> map = null;
		try {
			map = dBRepository.loadProductsFromFile(new File("./nuevos_ingresos.csv"));
		} catch (IOException e) {
			throw new RuntimeException();
		}

		List<ProductDetail> notFound = Lists.newArrayList();
		List<String> differentCoins = Lists.newArrayList();
		// System.out.println(map.keySet() + "\n\n");

		List<String> errorList = Lists.newArrayList();
		List<String> dbNames = dbList.stream().map(x -> Utils.normalize(x.getTitle())).collect(Collectors.toList());
		for (String name : map.keySet()) {
			if (!dbNames.contains(name)) {
				errorList.add(name);
			}
		}

		for (ProductDetail pf : dbList) {
			Product csvProduct = map.get(Utils.normalize(pf.getTitle()));
			if (csvProduct == null) {
				if (pf.getVisible()) {
					// notFound.add(pf.getTitle() + " No existe en el excel, VISIBLE: " +
					// pf.getVisible());
					notFound.add(pf);
//					System.out.println(pf.getTitle().toUpperCase().trim());
				}
				continue;
			}

			String dbCoins = pf.getWooCoins() == null ? "0.0" : pf.getWooCoins();
			if (!Double.valueOf(dbCoins).equals(csvProduct.getCoins())) {
				differentCoins.add(
						pf.getTitle() + " No tiene los coins iguales db " + dbCoins + " csv " + csvProduct.getCoins());
			}

		}
//		
//		notFound.stream().forEach(x -> System.out.println(x));
		differentCoins.stream().forEach(x -> System.out.println(x));
//		errorList.stream().forEach(x -> System.out.println(x));

//		for (ProductFull pf : notFound) {
//			System.out.println(pf.getId() + " " + pf.getTitle() + " ,");
//			System.out.println(pf.getTitle() + " No existe en el excel, VISIBLE: " + pf.getVisible());
//			productUpdaterService.hideProduct(pf.getId());
//		}

	}

	@Override
	public String getProductsCsvFromWoo() {
		List<ProductDetail> products = getProductsFromWoo();
		ProductToCsvSerializer serializer = new ProductToCsvSerializer();
		StringBuffer rows = new StringBuffer();
		rows.append(serializer.header());
		for (ProductDetail line : products) {
			rows.append(serializer.toString(line));
		}

		return rows.toString();
	}

	@Override
	public List<String> bulkUpsert(MultipartFile file) {
		List<String> output = new ArrayList<String>();

		try (Reader reader = new InputStreamReader(file.getInputStream());) {
			CSVReader csvReader = Utils.getCsvReaderUsingSeparator(reader, csvSeparator);

			// HEADER DE LOS RESULTADOS
			output.add(serializer.header());

			String[] currentLine;
			while ((currentLine = csvReader.readNext()) != null) {
				// inicializamos el builder con los datos de la fila
				StringBuilder sb = new StringBuilder();
				sb.append(String.join(csvSeparator, currentLine));

				List<String> resultados = new ArrayList<String>();
				if (currentLine.length < ROW_LENGTH) {
					sb.append(getErrorCantidadDeColumnas(sb.toString()));
					output.add(sb.toString());
					continue; // asi evitamos errores por null pointer
				}

				if (StringUtils.isBlank(currentLine[8])) {
					sb.append(csvSeparator + "Sin procesar - debe ingresar una Acción");
					output.add(sb.toString() + LINE_ENDING);
					continue; // si no se especifica una accion, no se procesa la fila
				}

				ProductDetailWithAction product = new ProductDetailWithAction();

				if (StringUtils.isBlank(currentLine[1])) {
					resultados.add("El nombre del producto es obligatorio");
				} else {
					if (currentLine[1].contains(csvSeparator)) {
						resultados.add("El nombre del producto no puede contener el caracter \"" + csvSeparator + "\"");
					} else {
						product.setTitle(StringUtils.normalizeSpace(currentLine[1]));
					}
				}

				// VALIDAMOS FORMATO DEL ID SOLO SI LA ACCION ES MODIFICAR
				if (CsvActionsEnum.MODIFICAR.getValue().equalsIgnoreCase(currentLine[8])
						&& !NumberUtils.isParsable(currentLine[0])) {
					resultados.add("El id debe ser numérico");
				} else if (CsvActionsEnum.MODIFICAR.getValue().equalsIgnoreCase(currentLine[8])
						&& NumberUtils.isParsable(currentLine[0])) {
					product.setId(Integer.parseInt(currentLine[0]));
				}

				if (StringUtils.isBlank(currentLine[4])) {
					resultados.add("El campo USD no puede quedar vacío");
				} else {
					product.setHxUsd(currentLine[4]);
				}

				if (StringUtils.isBlank(currentLine[5])) {
					resultados.add("El campo Currency no puede quedar vacío");
				} else {
					product.setHxCurrency(currentLine[5]);
				}

				if (StringUtils.isBlank(currentLine[6])) {
					resultados.add("El campo Currency Type no puede quedar vacío");
				} else {
					product.setHxCurrencyType(currentLine[6]);
				}

				// SE VALIDAN LOS DOS CAMPOS DE COINS Y SE OBTIENE UN VALOR
				// O BIEN UN MENSAJE DE ERROR
				String validCoins = getValidCoins(currentLine[2], currentLine[3]);
				if (validCoins.startsWith("error: ")) {
					resultados.add(StringUtils.remove(validCoins, "error: "));
				} else {
					product.setWooCoins(validCoins);
				}

				// SE VALIDA EL CAMPO VISIBLE
				String visibleValido = reportValidator.validateVisible(currentLine[7]);
				if (!StringUtils.EMPTY.equals(visibleValido)) {
					resultados.add(visibleValido);
				} else {
					product.setVisible(Boolean.parseBoolean(currentLine[7]));
				}

				// SE VALIDA EL CAMPO ACCION
				String accionValida = reportValidator.validateAction(currentLine[8]);
				if (!StringUtils.EMPTY.equals(accionValida)) {
					resultados.add(accionValida);
				} else {
					product.setAccion(currentLine[8]);
				}

				// SE VALIDA LA EXISTENCIA DEL ID, SOLO SI HAY UN ID NUMERICO Y LA ACCION ES
				// MODIFICAR
				if (product.getId() != null
						&& CsvActionsEnum.MODIFICAR.getValue().equalsIgnoreCase(product.getAccion())) {
					String noExiste = reportValidator.validateProductExistance(product.getId());
					if (!StringUtils.EMPTY.equals(noExiste)) {
						resultados.add(noExiste);
					}
				}

				if (!resultados.isEmpty()) {
					sb.append(csvSeparator);
					sb.append(String.join(" || ", resultados));
					sb.append(LINE_ENDING);
					output.add(sb.toString());
					continue;
				}

				// SI NO HAY ERRORES DETECTADOS HASTA EL MOMENTO SE HACE LA QUERY
				String finalResult;
				if (CsvActionsEnum.MODIFICAR.getValue().equalsIgnoreCase(product.getAccion())) {
					try {
						finalResult = productUpdaterService.updateWooCommerceDB(product);
						if (StringUtils.isBlank(finalResult)) {
							resultados.add("PROCESADO OK");
						} else {
							resultados.add(finalResult);
						}
					} catch (Exception ex) {
						resultados.add("HUBO UN ERROR AL MODIFICAR ESTE REGISTRO");
					}
				} else {
					Integer newId = null;
					try {
						newId = productUpdaterService.insertPostInWooCommerceDB(product);
						product.setId(newId);
						productUpdaterService.insertPostMetadata(product);
						sb.insert(0, getFirstToken(newId, sb));
						resultados.add("PROCESADO OK");
					} catch (Exception e) {
						if (newId != null) { // Se borra porque no se pudo insertar la metadata
							productUpdaterService.deletePost(newId);
						}
						resultados.add("HUBO UN ERROR AL INSERTAR ESTE REGISTRO");
					}
				}

				sb.append(csvSeparator);
				sb.append(String.join(" || ", resultados));
				sb.append(LINE_ENDING);
				output.add(sb.toString());
			}
		} catch (IOException e) {
			throw new CsvReadingException("ocurrió un error al leer el archivo csv");
		} catch (CsvValidationException e) {
			throw new CsvReadingException("ocurrió un error al leer el archivo csv");
		}

		return output;
	}

	private String getErrorCantidadDeColumnas(String fila) {
		int count = StringUtils.countMatches(fila, csvSeparator);
		String message = "";
		for (int i = count; i < fiedsPerRow; i++) {
			message += csvSeparator;
		}

		return message + "Faltan llenar columnas en esta fila" + LINE_ENDING;
	}

	private String getFirstToken(Integer token, StringBuilder sb) {
		if (csvSeparator.charAt(0) == sb.charAt(0))
			return Integer.toString(token);
		else
			return token + csvSeparator;
	}

	private String getValidCoins(String dbCoins, String csvCoins) {
		if (StringUtils.isBlank(dbCoins) && StringUtils.isBlank(csvCoins)) {
			return "error: Debe proveer un valor para el campo coins";
		} else if (StringUtils.isNotBlank(dbCoins) && StringUtils.isBlank(csvCoins)) {
			return dbCoins;
		} else if (StringUtils.isBlank(dbCoins) && StringUtils.isNotBlank(csvCoins)) {
			return csvCoins;
		} else if (!dbCoins.trim().equals(csvCoins.trim())) {
			return "error: Los campos coins (woo db) y coins (excel) deben tener el mismo valor";
		} else {
			return csvCoins;
		}
	}
}
