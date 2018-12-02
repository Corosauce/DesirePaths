package com.corosus.desirepaths.util;

import java.util.Iterator;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public class UtilEntityBuffsInstances {

    public static boolean replaceTaskIfMissing(EntityCreature ent, Class taskToReplace, EntityAIBase taskToReplaceWith) {
        int priority = Integer.MIN_VALUE;
        for (Iterator<EntityAITaskEntry> iter = ent.tasks.taskEntries.iterator(); iter.hasNext(); ) {
            EntityAITasks.EntityAITaskEntry entry = iter.next();
            if (taskToReplace.isAssignableFrom(entry.action.getClass())) {
            	priority = entry.priority;
                iter.remove();
                break;
            }
        }

        if (priority != Integer.MIN_VALUE) {
            addTask(ent, taskToReplaceWith, priority);
            return true;
        }

        return false;

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
