package com.t28.rxweather.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.t28.rxweather.request.ForecastRequest;
import com.t28.rxweather.util.CollectionUtils;
import com.t28.rxweather.volley.RxSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

@JsonDeserialize(builder = Forecast.Builder.class)
public class Forecast implements Model {
    private final City mCity;
    private final List<Weather> mWeathers;

    private Forecast(Builder builder) {
        mCity = builder.mCity;
        if (CollectionUtils.isEmpty(builder.mWeathers)) {
            mWeathers = Collections.emptyList();
        } else {
            mWeathers = new ArrayList<>(builder.mWeathers);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(Forecast.class.getSimpleName());

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonString = mapper.writeValueAsString(this);
            builder.append(jsonString);
            return builder.toString();
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }

    @Override
    public boolean isValid() {
        if (mCity == null || !mCity.isValid()) {
            return false;
        }

        for (Weather weather : mWeathers) {
            if (weather.isValid()) {
                continue;
            }
            return false;
        }
        return true;
    }

    public City getCity() {
        return mCity;
    }

    public List<Weather> getWeathers() {
        return new ArrayList<>(mWeathers);
    }

    public static Observable<Forecast> findByName(RxSupport support, String name) {
        final ForecastRequest request = new ForecastRequest.Builder("")
                .setCityName(name)
                .build();
        return support.createObservableRequest(request);
    }

    public static Observable<Forecast> findByCoordinate(RxSupport support, Coordinate coordinate) {
        final ForecastRequest request = new ForecastRequest.Builder("")
                .setLat(coordinate.getLat())
                .setLon(coordinate.getLon())
                .build();
        return support.createObservableRequest(request);
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
        private City mCity;
        private List<Weather> mWeathers;

        public Builder() {
        }

        public Builder setCity(City city) {
            mCity = city;
            return this;
        }

        @JsonProperty("list")
        public Builder setWeathers(List<Weather> weathers) {
            mWeathers = weathers;
            return this;
        }

        public Forecast build() {
            return new Forecast(this);
        }
    }
}