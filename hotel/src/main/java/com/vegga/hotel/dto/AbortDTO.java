package com.vegga.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbortDTO<T> {

    private BaseDTO<T> before;
    private BaseDTO<T> then;
}
