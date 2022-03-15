import React from 'react';
import { get, isEmpty } from 'lodash';

import type { IFormikStageConfigInjectedProps } from '@spinnaker/core';

import { YamlEditor, yamlDocumentsToString } from '@spinnaker/core';

interface IInputYamlForm {
  inputYaml: string;
}

export class InputYamlForm extends React.Component<IFormikStageConfigInjectedProps, IInputYamlForm> {
  constructor(props: IFormikStageConfigInjectedProps) {
    super(props);
    const yamlConfig: any[] = get(props.formik.values, 'yamlConfig');
    this.state = {
      inputYaml: !isEmpty(yamlConfig) ? yamlDocumentsToString(yamlConfig) : '',
    };
  }

  private handleRawManifestChange = (inputYaml: string, yamlConfig: any): void => {
    this.setState({ inputYaml });
    this.props.formik.setFieldValue('yamlConfig', yamlConfig);
  };

  public render() {
    return (
      <div className="form-horizontal">
        <h4>Yaml Configuration</h4>
        <YamlEditor value={this.state.inputYaml} onChange={this.handleRawManifestChange} />
      </div>
    );
  }
}
