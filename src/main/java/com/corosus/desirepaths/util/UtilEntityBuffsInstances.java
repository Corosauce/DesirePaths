package com.corosus.desirepaths.util;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

public class UtilEntityBuffsInstances {

    public static boolean replaceTaskIfMissing(EntityCreature ent, Class taskToReplace, EntityAIBase taskToReplaceWith, int priorityOfTask) {
        EntityAITasks.EntityAITaskEntry foundTask = null;
        for (Object entry2 : ent.tasks.taskEntries) {
            EntityAITasks.EntityAITaskEntry entry = (EntityAITasks.EntityAITaskEntry) entry2;
            if (taskToReplace.isAssignableFrom(entry.action.getClass())) {
                foundTask = entry;
                break;
            }
        }

        if (foundTask != null) {
            ent.tasks.taskEntries.remove(foundTask);

            addTask(ent, taskToReplaceWith, priorityOfTask);
        }

        return foundTask != null;

    }

    public static boolean addTask(EntityCreature ent, EntityAIBase taskToInject, int priorityOfTask) {
        try {
            ent.tasks.addTask(priorityOfTask, taskToInject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
