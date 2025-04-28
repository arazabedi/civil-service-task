"use client";

import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

import { TaskFormContent } from "./taskFormContent";

export function CreateTaskForm() {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button variant="outline">
          <span className="text-primary">Add Task</span>
        </Button>
      </PopoverTrigger>
      <PopoverContent>
        <TaskFormContent />
      </PopoverContent>
    </Popover>
  );
}
