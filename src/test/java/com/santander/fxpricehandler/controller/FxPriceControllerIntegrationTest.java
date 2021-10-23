package com.santander.fxpricehandler.controller;

import com.santander.fxpricehandler.cache.FxPriceCache;
import com.santander.fxpricehandler.service.FxPriceHandlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class FxPriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FxPriceCache fxPriceCache;

    @Autowired
    private FxPriceHandlerService fxPriceHandlerService;

    @Test
    void testPostPrice() throws Exception {
        assertEquals("Price successfully processed", mockMvc.perform(post("/postPrice")
                .contentType(MediaType.APPLICATION_JSON)
                .content("106,EUR/USD,1.0989,1.2012,01-06-2020 12:01:01:001"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        fxPriceCache.emptyCache();
    }

    @Test
    void testGetPriceInfo() throws Exception {
        //Publish price
        fxPriceHandlerService.onMessage("106,EUR/USD,1.0989,1.2012,01-06-2020 12:01:01:001");

        //Retrieve the price
        assertEquals("{\"id\":106,\"instrumentName\":\"EUR/USD\",\"bid\":1.0978011,\"ask\":1.2024012," +
                "\"timestamp\":\"2020-06-01T12:01:01.001\"}", mockMvc.perform(get("/getPrice/EUR/USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        fxPriceCache.emptyCache();
    }

    @Test
    void testPublishMultiplePricesAndGetPriceInfo() throws Exception {
        //Publish prices
        fxPriceHandlerService.onMessage("106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60, 119.90, 01-06-2020 12:01:01:002\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110\n" +
                "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n");

        //Retrieve the price
        assertEquals("{\"id\":109,\"instrumentName\":\"GBP/USD\",\"bid\":1.2486501,\"ask\":1.2573561," +
                "\"timestamp\":\"2020-06-01T12:01:02.1\"}", mockMvc.perform(get("/getPrice/GBP/USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        fxPriceCache.emptyCache();
    }

}
