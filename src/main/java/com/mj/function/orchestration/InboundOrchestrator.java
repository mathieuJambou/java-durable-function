package com.equisoft.function.orchestration;

import java.util.List;
import java.util.stream.Collectors;

import com.equisoft.function.entity.Command;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.Task;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

public class InboundOrchestrator {

    /**
     * @param ctx
     * @return
     */
    @FunctionName("InboudProcessor")
    public String inboudProcessorOrchestrator(
            @DurableOrchestrationTrigger(name = "taskOrchestrationContext") TaskOrchestrationContext ctx) {
        String result = "";

        Command command = ctx.getInput(Command.class);

        Boolean transformationResult = ctx.callActivity("Transformer", command, Boolean.class).await();

        if (transformationResult) {
            // Get the list of work-items to process in parallel
            final List<?> batch = ctx.callActivity("EntitiesRetriever", command, null, List.class).await();

            // Schedule each task to run in parallel
            List<Task<Boolean>> parallelTasks = batch.stream()
                    .map(item -> ctx.callActivity("EntityToAPIM", item, Boolean.class))
                    .collect(Collectors.toList());

            // Wait for all tasks to complete, then return the aggregated sum of the results
            List<Boolean> results = ctx.allOf(parallelTasks).await();

            return results.stream().distinct().toString();
        }

        return result;

    }

}
