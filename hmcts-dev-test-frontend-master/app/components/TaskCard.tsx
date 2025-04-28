import type { UniqueIdentifier } from "@dnd-kit/core";
import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { cva } from "class-variance-authority";
import { GripVertical, Trash2 } from "lucide-react";
import { Badge } from "./ui/badge";
import { ColumnId } from "./KanbanBoard";
import { deleteTask } from "../services/taskService";
import { toast } from "sonner";
export interface Task {
  id: UniqueIdentifier;
  title: string;
  columnId: ColumnId;
  description: string;
  dueDate: Date;
}

interface TaskCardProps {
  task: Task;
  isOverlay?: boolean;
}

export type TaskType = "Task";

export interface TaskDragData {
  type: TaskType;
  task: Task;
}

export function TaskCard({ task, isOverlay }: TaskCardProps) {
  const {
    setNodeRef,
    attributes,
    listeners,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: task.id,
    data: {
      type: "Task",
      task,
    } satisfies TaskDragData,
    attributes: {
      roleDescription: "Task",
    },
  });

  const style = {
    transition,
    transform: CSS.Translate.toString(transform),
  };

  const variants = cva("", {
    variants: {
      dragging: {
        over: "ring-2 opacity-30",
        overlay: "ring-2 ring-primary",
      },
    },
  });

  async function handleDelete(task) {
    try {
      console.log(task.id);
      deleteTask(task.id);
      toast.success("Task has been deleted", {
        description: "Task has been deleted",
        action: {
          label: "Undo",
          onClick: () => console.log("Undo"),
        },
      });
      window.location.reload();
    } catch (error) {
      if (error instanceof Error) {
        throw new Error("Failed to delete task: " + error.message);
      } else {
        throw new Error("Failed to delete task: An unknown error occurred");
      }
    }
  }

  return (
    <Card
      ref={setNodeRef}
      style={style}
      className={variants({
        dragging: isOverlay ? "overlay" : isDragging ? "over" : undefined,
      })}
    >
      <CardHeader className=" pb-3 flex flex-row items-center justify-between border-secondary relative">
        <Button
          variant={"ghost"}
          {...attributes}
          {...listeners}
          className="p-1 text-secondary-foreground/50 h-full cursor-grab"
        >
          <span className="sr-only">Move task</span>
          <GripVertical />
        </Button>
        <Badge>
          {task.dueDate.toLocaleDateString("en-US", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
          })}
        </Badge>
      </CardHeader>
      <CardContent className="text-left whitespace-pre-wrap">
        <h3 className="text-lg font-semibold">{task.title}</h3>
        {task.description}
        <Button
          className="hover:opacity-50 cursor-pointer"
          variant="destructive"
          onClick={() => handleDelete(task)}
        >
          <Trash2 />
        </Button>
      </CardContent>
    </Card>
  );
}
