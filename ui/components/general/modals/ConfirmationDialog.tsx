import { Dialog, Transition } from '@headlessui/react'
import { XMarkIcon } from '@heroicons/react/24/solid'
import { forwardRef, Fragment, useImperativeHandle, useRef, useState } from 'react'

interface ConfirmationDialogProps {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel?: () => void;
}

export const ConfirmationDialog = forwardRef(
  ({ title, message, confirmText = 'Confirm', cancelText = 'Cancel', onConfirm, onCancel }: ConfirmationDialogProps, ref) => {
    const [open, setOpen] = useState(false)
    const cancelButtonRef = useRef(null)
    ConfirmationDialog.displayName = 'ConfirmationDialog'

    useImperativeHandle(ref, () => ({
      open: () => {
        setOpen(true)
      },
      close: () => {
        setOpen(false)
      },
    }))

    const handleConfirm = () => {
      onConfirm();
      setOpen(false);
    }

    const handleCancel = () => {
      if (onCancel) onCancel();
      setOpen(false);
    }

    return (
      <Transition.Root show={open} as={Fragment}>
        <Dialog as="div" className="sm:relative z-30" initialFocus={cancelButtonRef} onClose={setOpen}>
          <Transition.Child
            as={Fragment}
            enter="ease-out duration-300"
            enterFrom="opacity-0"
            enterTo="opacity-100"
            leave="ease-in duration-200"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
          </Transition.Child>

          <div className="fixed inset-0 z-10 overflow-y-auto">
            <div className="flex min-h-full justify-center p-4 text-center items-center">
              <Transition.Child
                as={Fragment}
                enter="ease-out duration-300"
                enterFrom="opacity-0 translate-y-0 scale-95"
                enterTo="opacity-100 translate-y-0 scale-100"
                leave="ease-in duration-200"
                leaveFrom="opacity-100 translate-y-0scale-100"
                leaveTo="opacity-0 translate-y-0 scale-95"
              >
                <Dialog.Panel className="relative transform rounded-lg bg-white text-left shadow-xl transition-all my-8 w-full max-w-lg p-6">
                  <XMarkIcon 
                    className="absolute top-2 right-2 w-6 h-6 cursor-pointer" 
                    onClick={handleCancel} 
                  />
                  <Dialog.Title
                    as="h3"
                    className="text-lg font-medium leading-6 text-gray-900"
                  >
                    {title}
                  </Dialog.Title>
                  <div className="mt-2">
                    <p className="text-sm text-gray-500">
                      {message}
                    </p>
                  </div>

                  <div className="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      className="inline-flex justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
                      onClick={handleCancel}
                      ref={cancelButtonRef}
                    >
                      {cancelText}
                    </button>
                    <button
                      type="button"
                      className="inline-flex justify-center rounded-md border border-transparent bg-primary px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
                      onClick={handleConfirm}
                    >
                      {confirmText}
                    </button>
                  </div>
                </Dialog.Panel>
              </Transition.Child>
            </div>
          </div>
        </Dialog>
      </Transition.Root>
    )
  }
) 