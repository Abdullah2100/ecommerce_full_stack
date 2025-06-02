import { Input } from "./ui/input";

interface iInputWithTitleProp {
    title: string,
    name: string,
    placeHolder: string
    onchange: (value: string) => void,
}

const InputWithTitle = ({
    title,
    name,
    placeHolder,
    onchange
}: iInputWithTitleProp) => {

    return (<div className='w-full flex flex-col justify-center items-start'>
        <label>{title}</label>
        <div className='h-1' />
        <Input
            className='w-full'
            value={name}
            onChange={(e) => {
                e.preventDefault();
                const { value } = e.target
                onchange(value)
            }}
            placeholder={placeHolder}
        />
    </div>)
}

export default InputWithTitle;