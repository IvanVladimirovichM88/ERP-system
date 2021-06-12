package ru.geekbrains.erpsystem.data;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OperationParameterData {

    private String tag;
    private String value;

    @Override
    public String toString() {
        return String.format("{%s: %s}", tag, value);
    }
}
