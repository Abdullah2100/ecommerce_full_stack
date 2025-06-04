import { Input } from "./input";

interface iInputWithTitleProp {
    title: string,
    name: string,
    placeHolder: string
    isDisabled?: boolean,
    onchange?: (value: string) => void | undefined,
}

const InputWithTitle = ({
    title,
    name,
    placeHolder,
    isDisabled = false,
    onchange = undefined
}: iInputWithTitleProp) => {

    return (<div className='w-full flex flex-col justify-center items-start'>
        <label>{title}</label>
        <div className='h-1' />
        <Input
            disabled={isDisabled}
            className='w-full'
            value={name}
            onChange={(e) => {
                e.preventDefault();
                const { value } = e.target
                if (onchange)
                    onchange(value)
            }}
            placeholder={placeHolder}
        />
    </div>)
}

export default InputWithTitle;