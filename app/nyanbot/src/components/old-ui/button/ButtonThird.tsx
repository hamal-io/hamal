import Button, { ButtonProps } from "@/components/old-ui/button/Button";
import React from "react";

export interface ButtonThirdProps extends ButtonProps {}

const ButtonThird: React.FC<ButtonThirdProps> = ({
  className = "text-neutral-700 border border-neutral-200 dark:text-neutral-200 dark:border-neutral-700",
  ...args
}) => {
  return <Button className={`Third ${className}`} {...args} />;
};

export default ButtonThird;
