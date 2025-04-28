"use client";

import { useMemo, useRef, useState } from "react";
import { createPortal } from "react-dom";

import { BoardColumn, BoardContainer } from "./BoardColumn";
import {
  DndContext,
  type DragEndEvent,
  type DragOverEvent,
  DragOverlay,
  type DragStartEvent,
  useSensor,
  useSensors,
  KeyboardSensor,
  Announcements,
  UniqueIdentifier,
  TouchSensor,
  MouseSensor,
} from "@dnd-kit/core";
import { SortableContext, arrayMove } from "@dnd-kit/sortable";
import { type Task, TaskCard } from "./TaskCard";
import type { Column } from "./BoardColumn";
import { hasDraggableData } from "./utils";
import { coordinateGetter } from "./multipleContainersKeyboardPreset";

import { useEffect } from "react";
import { getAllTasks } from "../services/taskService";

const defaultCols = [
  {
    id: "pending" as const,
    title: "Pending",
  },
  {
    id: "in-progress" as const,
    title: "In progress",
  },
  {
    id: "completed" as const,
    title: "Completed",
  },
] satisfies Column[];

export type ColumnId = (typeof defaultCols)[number]["id"];

const initialTasks: Task[] = [
  {
    id: "task1",
    columnId: "pending",
    title: "Project initiation",
    description: "Kick off and define project scope and objectives.",
    dueDate: new Date("2025-05-05"),
  },
  {
    id: "task2",
    columnId: "pending",
    title: "Gather requirements",
    description:
      "Collect and document detailed requirements from all stakeholders.",
    dueDate: new Date("2025-05-10"),
  },
  {
    id: "task3",
    columnId: "pending",
    title: "Create wireframes",
    description: "Develop basic structural outlines of key website pages.",
    dueDate: new Date("2025-05-15"),
  },
  {
    id: "task4",
    columnId: "in-progress",
    title: "Homepage layout",
    description:
      "Develop the visual structure and elements of the main landing page.",
    dueDate: new Date("2025-05-20"),
  },
  {
    id: "task5",
    columnId: "in-progress",
    title: "Design color scheme",
    description:
      "Choose and implement the color palette and typography for the website.",
    dueDate: new Date("2025-05-25"),
  },
  {
    id: "task6",
    columnId: "completed",
    title: "Implement authentication",
    description: "Develop the user login and registration functionalities.",
    dueDate: new Date("2025-04-15"),
  },
  {
    id: "task7",
    columnId: "completed",
    title: "Build contact page",
    description:
      "Create the 'Contact Us' page with necessary forms and information.",
    dueDate: new Date("2025-04-18"),
  },
  {
    id: "task8",
    columnId: "completed",
    title: "Create product catalog",
    description: "Develop the section to display and manage product listings.",
    dueDate: new Date("2025-04-21"),
  },
  {
    id: "task9",
    columnId: "completed",
    title: "Develop about us page",
    description:
      "Create the 'About Us' page detailing the company's background and mission.",
    dueDate: new Date("2025-04-23"),
  },
  {
    id: "task10",
    columnId: "completed",
    title: "Optimize for mobile",
    description:
      "Ensure the website is responsive and functions correctly on various mobile devices.",
    dueDate: new Date("2025-04-24"),
  },
  {
    id: "task11",
    columnId: "completed",
    title: "Integrate payment gateway",
    description:
      "Implement the system for processing online payments securely.",
    dueDate: new Date("2025-04-25"),
  },
  {
    id: "task12",
    columnId: "completed",
    title: "Perform testing",
    description:
      "Conduct thorough testing to identify and fix any bugs or issues.",
    dueDate: new Date("2025-04-26"),
  },
  {
    id: "task13",
    columnId: "completed",
    title: "Launch website",
    description: "Deploy the completed website to the live server.",
    dueDate: new Date("2025-04-27"),
  },
];

export function KanbanBoard() {
  const [columns, setColumns] = useState<Column[]>(defaultCols);
  const pickedUpTaskColumn = useRef<ColumnId | null>(null);
  const columnsId = useMemo(() => columns.map((col) => col.id), [columns]);

  // const [tasks, setTasks] = useState<Task[]>(initialTasks);
  const [tasks, setTasks] = useState<Task[]>([]);

  const [activeColumn, setActiveColumn] = useState<Column | null>(null);

  const [activeTask, setActiveTask] = useState<Task | null>(null);

	const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    let isMounted = true; // To prevent setting state on unmounted components

    async function fetchTasks() {
      setIsClient(true);
      try {
        const allTasks = await getAllTasks();
        if (isMounted) {
          setTasks(allTasks);
        }
      } catch (error) {
        console.error("Error fetching tasks:", error);
        // Handle the error appropriately (e.g., set an error state)
      }
    }

    fetchTasks();

    return () => {
      isMounted = false; // Cleanup to prevent memory leaks
    };
  }, []);

  const sensors = useSensors(
    useSensor(MouseSensor),
    useSensor(TouchSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: coordinateGetter,
    })
  );

  function getDraggingTaskData(taskId: UniqueIdentifier, columnId: ColumnId) {
    const tasksInColumn = tasks.filter((task) => task.columnId === columnId);
    const taskPosition = tasksInColumn.findIndex((task) => task.id === taskId);
    const column = columns.find((col) => col.id === columnId);
    return {
      tasksInColumn,
      taskPosition,
      column,
    };
  }

  const announcements: Announcements = {
    onDragStart({ active }) {
      if (!hasDraggableData(active)) return;
      if (active.data.current?.type === "Column") {
        const startColumnIdx = columnsId.findIndex((id) => id === active.id);
        const startColumn = columns[startColumnIdx];
        return `Picked up Column ${startColumn?.title} at position: ${
          startColumnIdx + 1
        } of ${columnsId.length}`;
      } else if (active.data.current?.type === "Task") {
        pickedUpTaskColumn.current = active.data.current.task.columnId;
        const { tasksInColumn, taskPosition, column } = getDraggingTaskData(
          active.id,
          pickedUpTaskColumn.current
        );
        return `Picked up Task ${
          active.data.current.task.title
        } at position: ${taskPosition + 1} of ${
          tasksInColumn.length
					} in column ${column?.title}`;
				// TODO: Add update service here
      }
    },
    onDragOver({ active, over }) {
      if (!hasDraggableData(active) || !hasDraggableData(over)) return;

      if (
        active.data.current?.type === "Column" &&
        over.data.current?.type === "Column"
      ) {
        const overColumnIdx = columnsId.findIndex((id) => id === over.id);
        return `Column ${active.data.current.column.title} was moved over ${
          over.data.current.column.title
        } at position ${overColumnIdx + 1} of ${columnsId.length}`;
      } else if (
        active.data.current?.type === "Task" &&
        over.data.current?.type === "Task"
      ) {
        const { tasksInColumn, taskPosition, column } = getDraggingTaskData(
          over.id,
          over.data.current.task.columnId
        );
        if (over.data.current.task.columnId !== pickedUpTaskColumn.current) {
          return `Task ${
            active.data.current.task.title
          } was moved over column ${column?.title} in position ${
            taskPosition + 1
          } of ${tasksInColumn.length}`;
        }
        return `Task was moved over position ${taskPosition + 1} of ${
          tasksInColumn.length
        } in column ${column?.title}`;
			}
			// TODO: Add update service here

    },
    onDragEnd({ active, over }) {
      if (!hasDraggableData(active) || !hasDraggableData(over)) {
        pickedUpTaskColumn.current = null;
        return;
      }
      if (
        active.data.current?.type === "Column" &&
        over.data.current?.type === "Column"
      ) {
        const overColumnPosition = columnsId.findIndex((id) => id === over.id);

        return `Column ${
          active.data.current.column.title
        } was dropped into position ${overColumnPosition + 1} of ${
          columnsId.length
        }`;
      } else if (
        active.data.current?.type === "Task" &&
        over.data.current?.type === "Task"
      ) {
        const { tasksInColumn, taskPosition, column } = getDraggingTaskData(
          over.id,
          over.data.current.task.columnId
        );
        if (over.data.current.task.columnId !== pickedUpTaskColumn.current) {
          return `Task was dropped into column ${column?.title} in position ${
            taskPosition + 1
          } of ${tasksInColumn.length}`;
        }
        return `Task was dropped into position ${taskPosition + 1} of ${
          tasksInColumn.length
        } in column ${column?.title}`;
      }
			pickedUpTaskColumn.current = null;
			// TODO: Add update service here

    },
    onDragCancel({ active }) {
      pickedUpTaskColumn.current = null;
      if (!hasDraggableData(active)) return;
      return `Dragging ${active.data.current?.type} cancelled.`;
    },
  };

  return (
    <DndContext
      accessibility={{
        announcements,
      }}
      sensors={sensors}
      onDragStart={onDragStart}
      onDragEnd={onDragEnd}
      onDragOver={onDragOver}
    >
      <BoardContainer>
        <SortableContext items={columnsId}>
          {columns.map((col) => (
            <BoardColumn
              key={col.id}
              column={col}
              tasks={tasks.filter((task) => task.columnId === col.id)}
            />
          ))}
        </SortableContext>
      </BoardContainer>
      {isClient &&
        createPortal(
          <DragOverlay>
            {activeColumn && (
              <BoardColumn
                isOverlay
                column={activeColumn}
                tasks={tasks.filter(
                  (task) => task.columnId === activeColumn.id
                )}
              />
            )}
            {activeTask && <TaskCard task={activeTask} isOverlay />}
          </DragOverlay>,
          document.body
        )}
    </DndContext>
  );

  function onDragStart(event: DragStartEvent) {
    if (!hasDraggableData(event.active)) return;
    const data = event.active.data.current;
    if (data?.type === "Column") {
      setActiveColumn(data.column);
      return;
    }

    if (data?.type === "Task") {
      setActiveTask(data.task);
      return;
    }
  }

  function onDragEnd(event: DragEndEvent) {
    setActiveColumn(null);
    setActiveTask(null);

    const { active, over } = event;
    if (!over) return;

    const activeId = active.id;
    const overId = over.id;

    if (!hasDraggableData(active)) return;

    const activeData = active.data.current;

    if (activeId === overId) return;

    const isActiveAColumn = activeData?.type === "Column";
    if (!isActiveAColumn) return;

    setColumns((columns) => {
      const activeColumnIndex = columns.findIndex((col) => col.id === activeId);

      const overColumnIndex = columns.findIndex((col) => col.id === overId);

      return arrayMove(columns, activeColumnIndex, overColumnIndex);
    });
  }

  function onDragOver(event: DragOverEvent) {
    const { active, over } = event;
    if (!over) return;

    const activeId = active.id;
    const overId = over.id;

    if (activeId === overId) return;

    if (!hasDraggableData(active) || !hasDraggableData(over)) return;

    const activeData = active.data.current;
    const overData = over.data.current;

    const isActiveATask = activeData?.type === "Task";
    const isOverATask = overData?.type === "Task";

    if (!isActiveATask) return;

    // Im dropping a Task over another Task
    if (isActiveATask && isOverATask) {
      setTasks((tasks) => {
        const activeIndex = tasks.findIndex((t) => t.id === activeId);
        const overIndex = tasks.findIndex((t) => t.id === overId);
        const activeTask = tasks[activeIndex];
        const overTask = tasks[overIndex];
        if (
          activeTask &&
          overTask &&
          activeTask.columnId !== overTask.columnId
        ) {
          activeTask.columnId = overTask.columnId;
          return arrayMove(tasks, activeIndex, overIndex - 1);
        }

        return arrayMove(tasks, activeIndex, overIndex);
      });
    }

    const isOverAColumn = overData?.type === "Column";

    // Im dropping a Task over a column
    if (isActiveATask && isOverAColumn) {
      setTasks((tasks) => {
        const activeIndex = tasks.findIndex((t) => t.id === activeId);
        const activeTask = tasks[activeIndex];
        if (activeTask) {
          activeTask.columnId = overId as ColumnId;
          return arrayMove(tasks, activeIndex, activeIndex);
        }
        return tasks;
      });
    }
  }
}
