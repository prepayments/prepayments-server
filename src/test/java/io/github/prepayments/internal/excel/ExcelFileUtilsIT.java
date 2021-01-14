package io.github.prepayments.internal.excel;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.internal.model.PrepaymentDataEVM;
import io.github.prepayments.internal.model.sampleDataModel.CurrencyTableEVM;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static io.github.prepayments.internal.AppConstants.DATETIME_FORMATTER;
import static io.github.prepayments.internal.excel.ExcelTestUtil.readFile;
import static io.github.prepayments.internal.excel.ExcelTestUtil.toBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * If nothing is added in value for this test it confirms that the excel deserializer beans
 * are correctly configured. We are using the sample currency-table model from the internal
 * package but and if the user wishes additional tests can be added for custom data models.
 * The deserializer should work after being successfully injected into this test from a spring container.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
public class ExcelFileUtilsIT {

    @Autowired
    private ExcelFileDeserializer<CurrencyTableEVM> currencyTableEVMExcelFileDeserializer;

    @Autowired
    private ExcelFileDeserializer<PrepaymentDataEVM> deserializer;

    @Test
    public void deserializeCurrencyTableFile() throws Exception {

        // @formatter:off
        List<CurrencyTableEVM> currencies =
            currencyTableEVMExcelFileDeserializer.deserialize(toBytes(readFile("currencies.xlsx")));
        // @formatter:on

        assertThat(currencies.size()).isEqualTo(13);
        assertThat(currencies.get(0)).isEqualTo(CurrencyTableEVM.builder().rowIndex(1).country("USA").currencyCode("USD").currencyName("US DOLLAR").locality("FOREIGN").build());
        assertThat(currencies.get(1)).isEqualTo(CurrencyTableEVM.builder().rowIndex(2).country("UNITED KINGDOM").currencyCode("GBP").currencyName("STERLING POUND").locality("FOREIGN").build());
        assertThat(currencies.get(2)).isEqualTo(CurrencyTableEVM.builder().rowIndex(3).country("EURO-ZONE").currencyCode("EUR").currencyName("EURO").locality("FOREIGN").build());
        assertThat(currencies.get(3)).isEqualTo(CurrencyTableEVM.builder().rowIndex(4).country("KENYA").currencyCode("KES").currencyName("KENYA SHILLING").locality("LOCAL").build());
        assertThat(currencies.get(4)).isEqualTo(CurrencyTableEVM.builder().rowIndex(5).country("SWITZERLAND").currencyCode("CHF").currencyName("SWISS FRANC").locality("FOREIGN").build());
        assertThat(currencies.get(5)).isEqualTo(CurrencyTableEVM.builder().rowIndex(6).country("SOUTH AFRICA").currencyCode("ZAR").currencyName("SOUTH AFRICAN RAND").locality("FOREIGN").build());
        assertThat(currencies.get(12)).isEqualTo(CurrencyTableEVM.builder().rowIndex(13).country("CHINA").currencyCode("CNY").currencyName("CHINESE YUAN").locality("FOREIGN").build());
    }

    @Test
    public void prepaymentDataFile() throws Exception {

        List<PrepaymentDataEVM> evms = deserializer.deserialize(toBytes(readFile("prepaymentDataList.xlsx")));

        assertThat(evms.size()).isEqualTo(32813);

        for (int i = 0; i < 32813; i++) {
            String index = String.valueOf(i + 1);
            assertThat(evms.get(i))
                .isEqualTo(
                    PrepaymentDataEVM
                        .builder()
                        .rowIndex((long) (i + 1))
                        .accountName("accountName" + index)
                        .description("description" + index)
                        .accountNumber("accountNumber" + index)
                        .expenseAccountNumber("expenseAccountNumber" + index)
                        .prepaymentNumber("prepaymentNumber" + index)
                        .prepaymentDate(DATETIME_FORMATTER.format(LocalDate.of(1980, 1, 1).plusDays(i)))
                        .prepaymentAmount(Double.parseDouble(index) + 0.1)
                        .prepaymentPeriods(i + 1)
                        .build()
                );
        }
    }
}
