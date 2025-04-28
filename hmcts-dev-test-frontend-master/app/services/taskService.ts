import { Task } from "../components/TaskCard";
import { ColumnId } from "../components/KanbanBoard";

interface ServerTask {
	id: string;
	title: string;
	description: string;
	status: 'PENDING' | 'COMPLETED' | 'IN_PROGRESS';
	dueDateTime: string;
}

const statusMap: Record<ServerTask['status'], ColumnId> = {
	PENDING: 'pending',
	COMPLETED: 'completed',
	IN_PROGRESS: 'in-progress',
};

function convertServerTaskToTask(serverTask: ServerTask): Task {
	return {
		id: serverTask.id,
		title: serverTask.title,
		description: serverTask.description,
		columnId: statusMap[serverTask.status],
		dueDate: new Date(serverTask.dueDateTime),
	};
}

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
