
package com.equisoft.function.helpers;

import com.equisoft.function.entity.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandHelper {

    public static Command CreatecommandFromString(String inputCommand) {
        ObjectMapper mapper = new ObjectMapper();
        Command command = null;
        try {
            command = mapper.readValue(inputCommand, Command.class);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return command;
    }

}
