"use client";

import { zodResolver } from "@hookform/resolvers/zod";
// import { CalendarIcon } from "@radix-ui/react-icons";
import { format } from "date-fns";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";

import { createTask } from "@/app/services/taskService";
import { toast } from "sonner";

const FormSchema = z.object({
  title: z.string().describe("Title"),
  description: z.string().describe("Description"),
  status: z.enum(["PENDING", "IN_PROGRESS", "COMPLETED"]).describe("Status"),
  dueDateTime: z.date({
    required_error: "A date and time is required.",
  }),
});

export function TaskFormContent() {
  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      title: "",
      description: "",
      status: "PENDING",
      time: new Date(),
    },
  });

  async function onSubmit(data: z.infer<typeof FormSchema>) {
    const cleanedData = {
      title: data.title,
      description: data.description,
      status: data.status,
      dueDateTime: data.dueDateTime.toISOString().slice(0, 19),
    };
    try {
      createTask(cleanedData);
      toast("Task has been created", {
        description: "",
        action: {
          label: "Undo",
          onClick: () => console.log("Undo"),
        },
			});
			//refresh the page
			window.location.reload();
    } catch (error) {
      toast("Event has not been created", {
        description: "Sunday, December 03, 2023 at 9:00 AM",
        action: {
          label: "Undo",
          onClick: () => console.log("Undo"),
        },
      });
    }
  }

  function handleDateSelect(date: Date | undefined) {
    if (date) {
      form.setValue("dueDateTime", date);
    }
  }

  function handleTimeChange(type: "hour" | "minute", value: string) {
    const currentDate = form.getValues("dueDateTime") || new Date();
    const newDate = new Date(currentDate);

    if (type === "hour") {
      const hour = parseInt(value, 10);
      newDate.setHours(hour);
    } else if (type === "minute") {
      newDate.setMinutes(parseInt(value, 10));
    }

    form.setValue("dueDateTime", newDate);
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <input
                  type="text"
                  placeholder="Enter task title"
                  className="border-2 border-black h-9 w-full min-w-0 bg-transparent px-3 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none"
                  {...field}
                />
              </FormControl>
              <FormDescription>
                Please enter a title for your task.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Description</FormLabel>
              <FormControl>
                <input
                  type="text"
                  placeholder="Enter task description"
                  className="border-2 border-black h-9 w-full min-w-0 bg-transparent px-3 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none"
                  {...field}
                />
              </FormControl>
              <FormDescription>
                Please enter a description for your task.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Status</FormLabel>
              <FormControl>
                <select
                  className="border-2 border-black h-9 w-full min-w-0 bg-transparent px-3 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none"
                  {...field}
                >
                  <option value="PENDING">Pending</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </FormControl>
              <FormDescription>Please enter your task status.</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="dueDateTime"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel>Enter your date & time (24h)</FormLabel>
              <Popover>
                <PopoverTrigger asChild>
                  <FormControl>
                    <Button
                      variant={"outline"}
                      className={cn(
                        "w-full pl-3 text-left font-normal",
                        !field.value && "text-muted-foreground"
                      )}
                    >
                      {field.value ? (
                        format(field.value, "MM/dd/yyyy HH:mm")
                      ) : (
                        <span>MM/DD/YYYY HH:mm</span>
                      )}
                      {/* <CalendarIcon className="ml-auto h-4 w-4 opacity-50" /> */}
                    </Button>
                  </FormControl>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <div className="sm:flex">
                    <Calendar
                      mode="single"
                      selected={field.value}
                      onSelect={handleDateSelect}
                      initialFocus
                    />
                    <div className="flex flex-col sm:flex-row sm:h-[300px] divide-y sm:divide-y-0 sm:divide-x">
                      <ScrollArea className="w-64 sm:w-auto">
                        <div className="flex sm:flex-col p-2">
                          {Array.from({ length: 24 }, (_, i) => i)
                            .reverse()
                            .map((hour) => (
                              <Button
                                key={hour}
                                size="icon"
                                variant={
                                  field.value && field.value.getHours() === hour
                                    ? "default"
                                    : "ghost"
                                }
                                className="sm:w-full shrink-0 aspect-square"
                                onClick={() =>
                                  handleTimeChange("hour", hour.toString())
                                }
                              >
                                {hour}
                              </Button>
                            ))}
                        </div>
                        <ScrollBar
                          orientation="horizontal"
                          className="sm:hidden"
                        />
                      </ScrollArea>
                      <ScrollArea className="w-64 sm:w-auto">
                        <div className="flex sm:flex-col p-2">
                          {Array.from({ length: 12 }, (_, i) => i * 5).map(
                            (minute) => (
                              <Button
                                key={minute}
                                size="icon"
                                variant={
                                  field.value &&
                                  field.value.getMinutes() === minute
                                    ? "default"
                                    : "ghost"
                                }
                                className="sm:w-full shrink-0 aspect-square"
                                onClick={() =>
                                  handleTimeChange("minute", minute.toString())
                                }
                              >
                                {minute.toString().padStart(2, "0")}
                              </Button>
                            )
                          )}
                        </div>
                        <ScrollBar
                          orientation="horizontal"
                          className="sm:hidden"
                        />
                      </ScrollArea>
                    </div>
                  </div>
                </PopoverContent>
              </Popover>
              <FormDescription>
                Please select your preferred date and time.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button type="submit">Submit</Button>
      </form>
    </Form>
  );
}
