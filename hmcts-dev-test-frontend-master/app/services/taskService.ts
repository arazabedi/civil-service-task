import { Task } from "../components/TaskCard";
import { ColumnId } from "../components/KanbanBoard";
import { createTaskDto } from "../dto/taskDTO";
// Form of the task object returned from the server as JSON
interface ServerTask {
	id: string;
	title: string;
	description: string;
	status: 'PENDING' | 'COMPLETED' | 'IN_PROGRESS';
	dueDateTime: string;
}

// Object used to convert server task status to column id (see convertServerTaskToTask function)
const statusMap: Record<ServerTask['status'], ColumnId> = {
	PENDING: 'pending',
	COMPLETED: 'completed',
	IN_PROGRESS: 'in-progress',
};

// Object used to convert column id to server task status (see updateTaskStatus function)
const reverseStatusMap: Record<ColumnId, ServerTask['status']> = {
	'pending': 'PENDING',
	'completed': 'COMPLETED',
	'in-progress': 'IN_PROGRESS',
};

// Converts a server task object to a task object used in the app
function convertServerTaskToTask(serverTask: ServerTask): Task {
	return {
		id: serverTask.id,
		title: serverTask.title,
		description: serverTask.description,
		columnId: statusMap[serverTask.status],
		dueDate: new Date(serverTask.dueDateTime),
	};
}

// RETRIEVE ALL TASKS
export async function getAllTasks(): Promise<Task[]> {
	try {
		const response = await fetch(`http://localhost:${process.env.NEXT_PUBLIC_PORT}/tasks`);
		const serverTasks: ServerTask[] = await response.json();
		return serverTasks.map(convertServerTaskToTask);
	} catch (error: unknown) {
		if (error instanceof Error) {
			throw new Error("Failed to fetch tasks: " + error.message);
		} else {
			throw new Error("Failed to fetch tasks: An unknown error occurred");
		}
	}
}

// CREATE A NEW TASK
export async function createTask(task: createTaskDto): Promise<Task> {
	try {
		const response = await fetch(`http://localhost:${process.env.NEXT_PUBLIC_PORT}/tasks`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				title: task.title,
				description: task.description,
				status: task.status,
				dueDateTime: task.dueDateTime,
			}),
		});
		if (!response.ok) {
			throw new Error('Failed to create task');
		}
		const serverTask: ServerTask = await response.json();
		return convertServerTaskToTask(serverTask);
	} catch (error: unknown) {
		if (error instanceof Error) {
			throw new Error("Failed to create task: " + error.message);
		} else {
			throw new Error("Failed to create task: An unknown error occurred");
		}
	}
}

// UPDATE A TASK STATUS
export async function updateTaskStatus(taskId: string, status: ColumnId): Promise<Task> {
	try {
		const response = await fetch(`http://localhost:${process.env.NEXT_PUBLIC_PORT}/tasks/${taskId}`, {
			method: 'PATCH',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				status: reverseStatusMap[status],
			}),
		});
		if (!response.ok) {
			throw new Error('Failed to update task status');
		}
		const serverTask: ServerTask = await response.json();
		return convertServerTaskToTask(serverTask);
	} catch (error: unknown) {
		if (error instanceof Error) {
			throw new Error("Failed to update task status: " + error.message);
		} else {
			throw new Error("Failed to update task status: An unknown error occurred");
		}
	}
}

// DELETE A TASK
export async function deleteTask(taskId: string): Promise<void> {
	try {
		const response = await fetch(`http://localhost:${process.env.NEXT_PUBLIC_PORT}/tasks/${taskId}`, {
			method: 'DELETE',
		});
		if (!response.ok) {
			throw new Error('Failed to delete task');
		}
	} catch (error: unknown) {
		if (error instanceof Error) {
			throw new Error("Failed to delete task: " + error.message);
		} else {
			throw new Error("Failed to delete task: An unknown error occurred");
		}
	}
}
